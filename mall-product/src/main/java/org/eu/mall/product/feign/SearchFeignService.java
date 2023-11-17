package org.eu.mall.product.feign;

import org.eu.common.to.es.SkuEsModel;
import org.eu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("mall-search")
public interface SearchFeignService {
    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
