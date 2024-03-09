package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.SpuInfoEntity;
import org.eu.mall.product.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author kevin
 * @email drzhong2015 @gmail.com
 * @date 2023 -10-27 00:34:58
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    /**
     * Query page page utils.
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * Save spu info.
     *
     * @param vo the vo
     */
    void saveSpuInfo(SpuSaveVo vo);

    /**
     * Save base spu info.
     *
     * @param spuInfoEntity the spu info entity
     */
    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * Query page by condition page utils.
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPageByCondition(Map<String, Object> params);

    /**
     * 商品上架
     *
     * @param spuId the spu id
     */
    void up(Long spuId);

    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

