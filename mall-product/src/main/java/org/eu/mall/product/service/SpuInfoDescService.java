package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author kevin
 * @email drzhong2015 @gmail.com
 * @date 2023 -10-27 00:34:58
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    /**
     * Query page page utils.
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存spu的描述
     *
     * @param spuInfoDescEntity the spu info desc entity
     */
    void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity);
}

