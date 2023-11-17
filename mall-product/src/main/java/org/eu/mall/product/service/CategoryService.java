package org.eu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.product.entity.CategoryEntity;
import org.eu.mall.product.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author kevin
 * @email drzhong2015 @gmail.com
 * @date 2023 -10-27 00:34:58
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * Query page page utils.
     *
     * @param params the params
     * @return the page utils
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * List with tree list.
     *
     * @return the list
     */
    List<CategoryEntity> listWithTree();

    /**
     * Remove category by ids.
     *
     * @param catIds the cat ids
     */
    void removeCategoryByIds(List<Long> catIds);

    /**
     * Update cascade.
     *
     * @param category the category
     */
    void updateCascade(CategoryEntity category);

    /**
     * 查找catelogId的完整路径
     * [父/子/孙]
     *
     * @param catelogId the catelog id
     * @return long [ ]
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 查询一级分类
     *
     * @return the level 1 categorys
     */
    List<CategoryEntity> getLevel1Categorys();

    /**
     * 查询一级分类下二级分类和三级分类
     *
     * @return the catalog json
     */
    Map<String, List<Catelog2Vo>> getCatalogJson();
}

