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
 * @email drzhong2015@gmail.com
 * @date 2023-10-27 00:34:58
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo vo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

