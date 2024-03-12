package org.eu.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.eu.common.enume.OrderStatusEnum;
import org.eu.common.exception.NoStockException;
import org.eu.common.to.mq.OrderTo;
import org.eu.common.to.mq.StockDetailTo;
import org.eu.common.to.mq.StockLockedTo;
import org.eu.common.utils.R;
import org.eu.mall.ware.entity.WareOrderTaskDetailEntity;
import org.eu.mall.ware.entity.WareOrderTaskEntity;
import org.eu.mall.ware.feign.OrderFeignService;
import org.eu.mall.ware.feign.ProductFeignService;
import org.eu.common.to.SkuHasStockVo;
import org.eu.mall.ware.service.WareOrderTaskDetailService;
import org.eu.mall.ware.service.WareOrderTaskService;
import org.eu.mall.ware.vo.OrderItemVo;
import org.eu.mall.ware.vo.OrderVo;
import org.eu.mall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.ware.dao.WareSkuDao;
import org.eu.mall.ware.entity.WareSkuEntity;
import org.eu.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        /**
         * skuId: 1
         * wareId: 1
         */
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> entities = list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (entities == null || entities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            // 查询skuName
            // 如果失败 整个事务无需回滚 自己catch异常
            // todo 其他方式
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSkuEntity.setSkuName((String) data.get("skuName"));

                }
            } catch (Exception e) {

            }

            save(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        return skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            // 查询当前sku的总库存量
            // select SUM(stock- stock_locked) from `wms_ware_sku` where sku_id=1
            Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count != null && count > 0);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Long> listWareIdHasSkuStock(Long skuId) {
        return baseMapper.listWareIdHasSkuStock(skuId);
    }

    /**
     * 为某个订单锁定库存
     *
     * (rollbackFor = NoStockException.class)
     * 默认只要是运行时异常都会回滚
     * 库存解锁的场景
     * 1）、下订单成功，订单过期没有支付被系统自动取消、被用户手动取消。都要解锁库存
     * 2）、下订单成功，库存锁定成功，接下来的业务调用失败，导致订单回滚。
     *     之前锁定的库存就要自动解锁。
     * @param vo
     * @return
     */
    //@Transactional(rollbackFor = NoStockException.class)
    @Transactional
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // 保存库存工作单详情 方便追溯
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskService.save(taskEntity);

        //按照下单的收货地址，找到一个就近仓库，锁定库存。
        //找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            skuWareHasStock.setSkuId(skuId);
            skuWareHasStock.setNum(item.getCount());
            // 查询当前sku在哪里有库存
            List<Long> wareIds = listWareIdHasSkuStock(skuId);
            skuWareHasStock.setWareId(wareIds);
            return skuWareHasStock;
        }).collect(Collectors.toList());
        // 锁定库存
        for (SkuWareHasStock skuWareHasStock : collect) {
            boolean skuStocked = false;
            Long skuId = skuWareHasStock.getSkuId();
            List<Long> wareIds = skuWareHasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }
            //1、如果每一个商品都锁定成功，将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存的工作单信息就回滚了。发送出去的消息，即使要解锁记录，由于去数据库查不到id，所以就不用解锁
            //     1： 1 - 2 - 1   2：2-1-2  3：3-1-1(x)
            for (Long wareId : wareIds) {
                Long lockSkuStock = baseMapper.lockSkuStock(skuId, wareId, skuWareHasStock.getNum());
                if (lockSkuStock == 1) {
                    skuStocked = true;
                    //告诉MQ库存锁定成功
                    WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), taskEntity.getId(), wareId, 1);
                    wareOrderTaskDetailService.save(entity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(entity, stockDetailTo);
                    //只发id不行，防止回滚以后找不到数据
                    lockedTo.setDetail(stockDetailTo);

                    rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", lockedTo);
                    break;
                }else {
                    // 当前仓库锁失败，重试下一个仓库
                }
            }
            if(!skuStocked){
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }
        //锁定成功
        return true;
    }

    // 内部类
    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

    @Override
    public void unlockStock(StockLockedTo to) {
        //try {
            StockDetailTo detail = to.getDetail();
            Long detailId = detail.getId();
            // 解锁
            //1、查询数据库关于这个订单的锁定库存信息。
            //  有：证明库存锁定成功了
            //    解锁：订单情况。
            //          1、没有这个订单。必须解锁
            //          2、有这个订单。不是解锁库存。
            //                订单状态： 已取消：解锁库存
            //                          没取消：不能解锁
            //  没有：库存锁定失败了，库存回滚了。这种情况无需解锁
            WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailId);
            if (byId != null) {
                // 解锁
                Long id = to.getId();
                WareOrderTaskEntity taskEntity = wareOrderTaskService.getById(id);
                String orderSn = taskEntity.getOrderSn();// 根据订单号查询订单的状态
                R r = orderFeignService.getOrderStatus(orderSn);
                if (r.getCode() == 0) {
                    // 订单数据返回成功
                    OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
                    });
                    if (orderVo == null || orderVo.getStatus().equals(OrderStatusEnum.CANCLED.getCode())) {
                        // 订单不存在 (订单回滚了 库存锁定)
                        // 订单被取消 解锁库存
                        if (byId.getLockStatus() == 1) {
                            // 当前工作单详情为已锁定
                            unlockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                            //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                        }
                    }
                } else {
                    // 消息拒绝以后 重新放到队列里面 让别人继续消费解锁
                    //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                    throw new RuntimeException("远程服务失败");
                }
            } else {
                // 无需解锁
                //channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
        //}catch (Exception e) {
            // 消息拒绝以后 重新放到队列里面 让别人继续消费解锁
            //System.out.println("错误..." + e.getMessage());
            //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        //}
    }
    /**
     * 解锁库存 更新库存和工作详情单
     *
     * @param skuId
     * @param wareId
     * @param skuNum
     * @param detailId
     */
    private void unlockStock(Long skuId, Long wareId, Integer skuNum, Long detailId) {
        //update `wms_ware_sku`set stock_locked = stock_locked - #{num} where sku_id = #{skuId} and ware_id = #{wareId}
        baseMapper.unlockStock(skuId, wareId, skuNum);
        // 更新工作单详情状态
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(detailId);
        entity.setLockStatus(2); // 已解锁
        wareOrderTaskDetailService.updateById(entity);
    }
    @Override
    public void unlockStock(OrderTo orderTo) {

    }
}
