package org.eu.mall.product.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.eu.common.exception.BizCodeEnum;
import org.eu.common.utils.R;
import org.eu.mall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class SeckillFeignServiceFallback implements SeckillFeignService {
    @Override
    public R getSkuSeckillInfo(Long skuId) {
        log.info("熔断方法调用 getSkuSeckillInfo");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
