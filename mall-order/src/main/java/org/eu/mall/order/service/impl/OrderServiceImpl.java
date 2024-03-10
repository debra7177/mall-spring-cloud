package org.eu.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.eu.common.utils.R;
import org.eu.common.vo.MemberRespVo;
import org.eu.mall.order.constant.OrderConstant;
import org.eu.mall.order.entity.OrderItemEntity;
import org.eu.mall.order.enume.OrderStatusEnum;
import org.eu.mall.order.feign.CartFeignService;
import org.eu.mall.order.feign.MemberFeignService;
import org.eu.mall.order.feign.ProductFeignService;
import org.eu.mall.order.feign.WmsFeignService;
import org.eu.mall.order.interceptor.LoginUserInterceptor;
import org.eu.mall.order.service.OrderItemService;
import org.eu.mall.order.to.OrderCreateTo;
import org.eu.mall.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.order.dao.OrderDao;
import org.eu.mall.order.entity.OrderEntity;
import org.eu.mall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartFeignService cartFeignService;

    @Autowired
    private WmsFeignService wmsFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        System.out.println("主线程" + Thread.currentThread().getId());

        // 获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //远程查询所有的收货地址列表
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            System.out.println("member线程" + Thread.currentThread().getId());
            // 每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
        }, threadPoolExecutor);
        //远程查询购物车所有选中的购物项
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            System.out.println("购物车线程" + Thread.currentThread().getId());
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            //feign在远程调用之前要构造请求，调用很多的拦截器
            //RequestInterceptor interceptor : requestInterceptors
        }, threadPoolExecutor).thenRunAsync(() -> {
            // 批量查询sku库存
            List<OrderItemVo> items = confirmVo.getItems();
            if (items != null && items.size() > 0) {
                List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
                // 一定要启动库存服务，否则库存查不出。
                R skusHasStock = wmsFeignService.getSkusHasStock(collect);
                List<SkuStockVo> data = skusHasStock.getData(new TypeReference<List<SkuStockVo>>() {
                });
                if (data != null) {
                    Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                    confirmVo.setStocks(map);
                }
            }
        }, threadPoolExecutor);
        // 查询用户积分
        Integer integration = memberRespVo.getIntegration();
        confirmVo.setIntegration(integration);
        //4、其他数据自动计算

        //5、防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture, cartFuture).get();
        return confirmVo;
    }

    //下单：去创建订单，验令牌，验价格，锁库存...
    //本地事务，在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
    //分布式事务： 最大原因。网络问题 +分布式机器。
    //(isolation = Isolation.REPEATABLE_READ)
    //REQUIRED、REQUIRES_NEW
    //@GlobalTransactional
    // 这里是高并发场景 使用 alibaba Seata AT 模式效率不高
    // 也不使用 TCC事务补偿方案
    // 采用 最大努力通知方案(BASE理论) 可靠消息 + 最终一致性
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        confirmVoThreadLocal.set(vo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        response.setCode(0);
        //1、验证令牌【令牌的对比和删除必须保证原子性】
        //0令牌失败 - 1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = vo.getOrderToken();
        //原子验证令牌和删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberRespVo.getId()), orderToken);
        if (result == 0L) {
            //令牌验证失败
            response.setCode(1);
            return response;
        }else {
            //令牌验证成功
            //下单：去创建订单，验令牌，验价格，锁库存...
            //1、创建订单，订单项等信息
            OrderCreateTo order = createOrder();
            //2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            // 金额差 不能大于1分
            if (Math.abs(payAmount.subtract(vo.getPayPrice()).doubleValue()) < 0.01) {
                saveOrder(order);
                //4、库存锁定。只要有异常回滚订单数据。
                // 订单号，所有订单项（skuId，skuName，num）
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(locks);
                // feign 调用锁库存
                //库存成功了，但是网络原因超时了，订单回滚，库存不滚。
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    // 锁定成功
                    response.setOrder(order.getOrder());
                    //TODO 5、远程扣减积分 出异常
                    int i = 10/0; //订单回滚，库存不滚
                    return response;
                }else {
                    // 锁定失败
                    response.setCode(3);
                    return response;
                }
            }else {
                response.setCode(2);
                return response;
            }
        }
    }

    /**
     * 保存订单数据
     *
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        // order
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        save(orderEntity);
        // orderItem
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
        // seata 0.7.1会遇到无法批量插入 可以改为增强for循环单个插入 也可以升级seata-all到 0.9.0
        // @link https://blog.csdn.net/jinjinbu/article/details/127733826
        //for (OrderItemEntity orderItem : orderItems) {
        //    orderItemService.save(orderItem);
        //}
    }

    private OrderCreateTo createOrder() {
        OrderCreateTo createTo = new OrderCreateTo();
        //1、生成订单号
        String orderSn = IdWorker.getTimeId();

        //创建订单
        OrderEntity orderEntity = buildOrder(orderSn);

        //2、获取到所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);

        //3、计算价格、积分等相关
        computePrice(orderEntity, itemEntities);

        createTo.setOrder(orderEntity);
        createTo.setOrderItems(itemEntities);

        return createTo;
    }

    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");

        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");

        BigDecimal gift =new BigDecimal("0.0");
        BigDecimal growth =new BigDecimal("0.0");

        // 根据订单项计算订单各项总额
        for (OrderItemEntity itemEntity : itemEntities) {
            total = total.add(itemEntity.getRealAmount());
            coupon = coupon.add(itemEntity.getCouponAmount());
            integration = integration.add(itemEntity.getIntegrationAmount());
            promotion = promotion.add(itemEntity.getPromotionAmount());
            gift = gift.add(new BigDecimal(itemEntity.getGiftGrowth().toString()));
            growth = growth.add(new BigDecimal(itemEntity.getGiftIntegration().toString()));
        }
        // 订单金额
        orderEntity.setTotalAmount(total);
        // 应付总额 = 订单金额 + 运费
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);

        // 积分和成长值
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        orderEntity.setDeleteStatus(0); //未删除
    }

    /**
     * 构建所有订单项数据
     *
     * @param orderSn
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 确定每个购物项的价格 从缓存中取出购物项数据
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
            return currentUserCartItems.stream().map(item -> {
                OrderItemEntity itemEntity = buildOrderItem(item);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 构建单个订单项
     *
     * @param item
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //spu信息
        Long skuId = item.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId().toString()); // 品牌id
        itemEntity.setSpuName(data.getSpuName());
        itemEntity.setCategoryId(data.getCatalogId());
        // sku信息
        itemEntity.setSkuId(item.getSkuId());
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPrice(item.getPrice());
        itemEntity.setSkuPic(item.getImage());
        // list 转字符串
        itemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(item.getSkuAttr(), ";"));
        itemEntity.setSkuQuantity(item.getCount()); // 商品购买的数量
        // todo 订单优惠
        // 积分和成长值 单价x数量
        itemEntity.setGiftGrowth(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue()); // 赠送成长值
        itemEntity.setGiftIntegration(item.getPrice().multiply(new BigDecimal(item.getCount().toString())).intValue()); // 赠送积分

        // 订单项的价格信息
        itemEntity.setPromotionAmount(new BigDecimal("0")); // 商品促销分解金额
        itemEntity.setCouponAmount(new BigDecimal("0")); // 优惠券优惠分解金额
        itemEntity.setIntegrationAmount(new BigDecimal("0")); // 积分优惠分解金额
        // 订单项实际金额 = 总额 - 优惠券 - 积分 - 商品促销
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        BigDecimal real = origin.subtract(itemEntity.getCouponAmount())
                .subtract(itemEntity.getIntegrationAmount())
                .subtract(itemEntity.getPromotionAmount());
        itemEntity.setRealAmount(real);
        return itemEntity;
    }

    /**
     * 创建订单基本信息
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(memberRespVo.getId());

        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        R fare = wmsFeignService.getFare(submitVo.getAddrId());
        FareVo fareData = fare.getData(new TypeReference<FareVo>() {
        });
        // 设置运费信息
        orderEntity.setFreightAmount(fareData.getFare());
        // 设置收货人信息
        orderEntity.setReceiverCity(fareData.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareData.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareData.getAddress().getName());
        orderEntity.setReceiverPhone(fareData.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareData.getAddress().getPostCode());
        orderEntity.setReceiverProvince(fareData.getAddress().getProvince());
        orderEntity.setReceiverRegion(fareData.getAddress().getRegion());

        // 设置订单状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);

        return orderEntity;
    }
}
