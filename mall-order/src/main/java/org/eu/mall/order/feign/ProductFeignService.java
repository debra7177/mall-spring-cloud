package org.eu.mall.order.feign;

import org.eu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mall-product")
public interface ProductFeignService {
    /**
     * 根据skuId查询spuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/product/spuinfo/skuId/{id}")
    R getSpuInfoBySkuId(@PathVariable("id") Long skuId);
}
