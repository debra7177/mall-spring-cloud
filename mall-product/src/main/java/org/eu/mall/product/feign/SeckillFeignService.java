package org.eu.mall.product.feign;

import org.eu.common.utils.R;
import org.eu.mall.product.feign.fallback.SeckillFeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mall-seckill", fallback = SeckillFeignServiceFallback.class)
public interface SeckillFeignService {
    /**
     * 查询商品是否参加秒杀活动 参与则返回场次信息和sku信息
     * @param skuId
     * @return
     */
    @GetMapping("/sku/seckill/{skuId}")
    R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
