package org.eu.mall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "mall-product")
public interface ProductFeignService {
}
