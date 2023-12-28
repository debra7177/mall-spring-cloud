package org.eu.mall.product.vo;

import lombok.Data;
import org.eu.mall.product.entity.SkuImagesEntity;
import org.eu.mall.product.entity.SkuInfoEntity;
import org.eu.mall.product.entity.SpuInfoDescEntity;

import java.util.List;

@Data
public class SkuItemVo {

    // 1. sku基本信息获取
    SkuInfoEntity info;

    boolean hasStock = true;

    // 2. sku的图片信息 pms_sku_images
    List<SkuImagesEntity> images;

    // 3. sku的销售属性组合
    List<SkuItemSaleAttrVo> saleAttr;

    // 4. spu 的介绍
    SpuInfoDescEntity desp;

    // 5. 获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;

    SeckillInfoVo seckillInfo;//当前商品的秒杀优惠信息
}
