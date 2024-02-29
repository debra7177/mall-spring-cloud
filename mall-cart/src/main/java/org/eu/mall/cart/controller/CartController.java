package org.eu.mall.cart.controller;

import lombok.extern.slf4j.Slf4j;
import org.eu.mall.cart.interceptor.CartInterceptor;
import org.eu.mall.cart.service.CartService;
import org.eu.mall.cart.vo.Cart;
import org.eu.mall.cart.vo.CartItem;
import org.eu.mall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@Slf4j
public class CartController {
    @Autowired
    private CartService cartService;

    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        //UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //log.info("userInfoTo:{}", userInfoTo);
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);

        return "cartList";
    }

    /**
     * 添加购物车(购物项)
     * RedirectAttributes ra
     *      ra.addFlashAttribute();将数据放在session里面可以在页面取出，但是只能取一次
     *      ra.addAttribute("skuId",skuId);将数据放在url后面
     * @param skuId
     * @param num
     * @param redirectAttributes
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.vmake.eu.org/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccess(@RequestParam("skuId") Long skuId, Model model) {
        CartItem item = cartService.getCartItem(skuId);
        model.addAttribute("item", item);
        return "success";
    }

    /**
     * 获取用户被选中的购物项
     * @return
     */
    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> currentUserCartItems() {
        return cartService.getUserCartItems();
    }
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.vmake.eu.org/cart.html";
    }

    /**
     * 更新购物项数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num) throws ExecutionException, InterruptedException {
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.vmake.eu.org/cart.html";
    }
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.vmake.eu.org/cart.html";
    }
}
