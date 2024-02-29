package org.eu.mall.cart.service;

import org.eu.mall.cart.vo.Cart;
import org.eu.mall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {
    /**
     * 获取整个购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中某个购物项
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 清空购物车
     * @param cartKey
     */
    void  clearCart(String cartKey);

    /**
     * 获取用户购物车中所有被选中的购物项
     * @return
     */
    List<CartItem> getUserCartItems();

    /**
     * 删除购物车中某个购物项
     * @param skuId
     */
    void deleteItem(Long skuId);

    /**
     * 修改购物车中某个购物项的数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 修改购物车中某个购物项的选中状态
     * @param skuId
     * @param check
     */
    void checkItem(Long skuId, Integer check);
}
