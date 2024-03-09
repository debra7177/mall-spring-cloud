package org.eu.mall.order.vo;

import lombok.Data;

/**
 * sku是否有库存
 */
@Data
public class SkuStockVo {
    private Long skuId;
    private Boolean hasStock;
}
