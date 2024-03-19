package org.eu.mall.seckill.feign;

import org.eu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("mall-coupon")
public interface CouponFeignService {
    /**
     * 查询最近三天需要参加秒杀商品的信息
     *
     * @return
     */
    @GetMapping("/coupon/seckillsession/latest3DaySession")
    R getLatest3DaySession();
}
