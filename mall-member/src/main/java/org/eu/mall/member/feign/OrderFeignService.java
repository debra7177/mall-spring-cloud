package org.eu.mall.member.feign;

import org.eu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient(value = "mall-order")
public interface OrderFeignService {
    /**
     * 查询当前登录用户使所有订单
     * @param params
     * @return
     */
    @RequestMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);
}
