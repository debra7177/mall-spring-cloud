package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.AttrAttrgroupRelationEntity;
import org.eu.mall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-27 00:34:58
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntityList);

    /**
     * 批量保存属性分组 - 属性关联关系
     * @param vos
     */
    void saveBatch(List<AttrGroupRelationVo> vos);
}

