package org.eu.mall.order.feign;

import org.eu.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "mall-cart")
public interface CartFeignService {
    // 远程查询购物车所有选中的购物项
    @GetMapping("/currentUserCartItems")
    List<OrderItemVo> getCurrentUserCartItems();
}
