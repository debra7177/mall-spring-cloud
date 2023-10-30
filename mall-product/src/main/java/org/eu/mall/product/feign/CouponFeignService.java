package org.eu.mall.product.feign;

import org.eu.common.to.SkuReductionTo;
import org.eu.common.to.SpuBoundTo;
import org.eu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("mall-coupon")
public interface CouponFeignService {
    // 保存spu的积分信息 gulimall_sms -> sms_spu_bounds
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);

    // sku的优惠 满减等信息 gulimall_sms-> sms_sku_ladder sms_sku_full_reduction sms_member_price
    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
