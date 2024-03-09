package org.eu.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.eu.common.utils.R;
import org.eu.mall.cart.feign.ProductFeignService;
import org.eu.mall.cart.interceptor.CartInterceptor;
import org.eu.mall.cart.service.CartService;
import org.eu.mall.cart.vo.Cart;
import org.eu.mall.cart.vo.CartItem;
import org.eu.mall.cart.vo.SkuInfoVo;
import org.eu.mall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    private static final String CART_KEY_PREFIX = "mall:cart:";

    /**
     * 获取要操作的购物车缓存对象
     * @return
     */
    public BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey;
        if (userInfoTo.getUserId() != null) {
            cartKey = CART_KEY_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CART_KEY_PREFIX + userInfoTo.getUserKey();
        }
        return stringRedisTemplate.boundHashOps(cartKey);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException{
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 用户登录状态
            String cartKey = CART_KEY_PREFIX + userInfoTo.getUserId();
            // 如果有临时购物车数据没有合并 则合并购物车
            String tempCartKey = CART_KEY_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(tempCartKey);
            if (cartItems != null && cartItems.size() > 0) {
                for (CartItem item : cartItems) {
                    this.addToCart(item.getSkuId(), item.getCount());
                }
                // 清空临时购物车
                clearCart(tempCartKey);
            }
            // 获取登录的购物车数据
            cart.setItems(getCartItems(cartKey));
        }else {
            // 用户未登录
            String tempCartKey = CART_KEY_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(tempCartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOperations.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((item) -> JSON.parseObject((String) item, CartItem.class)).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        // 1.判断购物车中是否有该商品
        if (StringUtils.isEmpty(res)) {
            // 没有则新增
            // 异步获取skuInfo
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R skuInfo = productFeignService.getSkuInfo(skuId);
                SkuInfoVo infoVo = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                cartItem.setImage(infoVo.getSkuDefaultImg());
                cartItem.setTitle(infoVo.getSkuTitle());
                cartItem.setPrice(infoVo.getPrice());
            }, executor);
            // 异步获取skuAttr
            CompletableFuture<Void> getSkuAttrTask = CompletableFuture.runAsync(() -> {
                // 获取sku的组合信息
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);
            CompletableFuture.allOf(getSkuInfoTask, getSkuAttrTask).get();
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }
        // 修改已有的购物项
        CartItem cartItem = JSON.parseObject(res, CartItem.class);
        cartItem.setCount(cartItem.getCount() + num);
        cartItem.setCheck(true);
        cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        return cartItem;
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartItem = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(cartItem, CartItem.class);
    }

    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }


    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        }
        String cartKey = CART_KEY_PREFIX + userInfoTo.getUserId();
        List<CartItem> cartItems = getCartItems(cartKey);
        // 获取所有被选中的购物项
        if (cartItems != null && cartItems.size() > 0) {
            return cartItems.stream().filter(CartItem::getCheck).map((item) -> {
                // 获取价格
                R price = productFeignService.getPrice(item.getSkuId());
                item.setPrice(price.getData(new TypeReference<BigDecimal>() {
                }));
                return item;
            }).collect(Collectors.toList());
        }
        return cartItems;
    }

    @Override
    public void deleteItem(Long skuId) {
        getCartOps().delete(skuId.toString());
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String res = (String) cartOps.get(skuId.toString());
        if (!StringUtils.isEmpty(res)) {
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }else {
            this.addToCart(skuId, num);
        }
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        getCartOps().put(skuId.toString(), JSON.toJSONString(cartItem));
    }
}
