package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.AttrEntity;
import org.eu.mall.product.vo.AttrGroupRelationVo;
import org.eu.mall.product.vo.AttrResponseVo;
import org.eu.mall.product.vo.AttrVo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author kevin
 * @email drzhong2015 @gmail.com
 * @date 2023 -10-27 00:34:58
 */
public interface AttrService extends IService<AttrEntity> {

    /**
     * Query page page utils.
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * Save attr.
     *
     * @param attrVo the attr vo
     */
    void saveAttr(AttrVo attrVo);


    /**
     * 查询当前三级分类下的所有基本属性
     *
     * @param params    the params
     * @param catelogId the catelog id
     * @param type      the type
     * @return the page utils
     */
    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    /**
     * Gets attr info.
     *
     * @param attrId the attr id
     * @return the attr info
     */
    AttrResponseVo getAttrInfo(Long attrId);

    /**
     * Update attr.
     *
     * @param attr the attr
     */
    void updateAttr(AttrVo attr);

    /**
     * Gets relation attr.
     *
     * @param attrGroupId the attr group id
     * @return the relation attr
     */
    List<AttrEntity> getRelationAttr(Long attrGroupId);

    /**
     * 删除基本属性 - 属性分组关联关系
     *
     * @param vos the vos
     */
    void deleteRelation(AttrGroupRelationVo[] vos);

    /**
     * Gets no relation attr.
     *
     * @param params      the params
     * @param attrGroupId the attr group id
     * @return the no relation attr
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId);


    /**
     * 在指定的所有属性集合里面 挑出检索属性
     *
     * @param attrIds the attr ids
     * @return the list
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

