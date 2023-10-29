package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.BrandEntity;
import org.eu.mall.product.entity.CategoryBrandRelationEntity;
import org.eu.mall.product.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author kevin
 * @email drzhong2015 @gmail.com
 * @date 2023 -10-27 00:34:58
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    /**
     * 查询所有
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 更新关系中三级分类部分
     *
     * @param catId the cat id
     * @param name  the name
     */
    void updateCategory(Long catId, String name);

    /**
     * 保存关系详情(包含冗余字段)
     *
     * @param categoryBrandRelation the category brand relation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 更新关系中品牌部分
     *
     * @param brandId the brand id
     * @param name    the name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 通过三级分类获取所有品牌
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

