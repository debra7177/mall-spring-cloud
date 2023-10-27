package org.eu.mall.product.service.impl;

import org.eu.mall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.product.dao.CategoryDao;
import org.eu.mall.product.entity.CategoryEntity;
import org.eu.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryEntity> collect = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map(item -> {
                    item.setChildren(getChildren(item, entities));
                    return item;
                })
                .sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort())
                        - (menu2.getSort() == null ? 0 : menu2.getSort()))
                .collect(Collectors.toList());
        return collect;
    }

    // 递归分类category
    private List<CategoryEntity> getChildren(CategoryEntity item, List<CategoryEntity> entitiesAll) {
        List<CategoryEntity> collect = entitiesAll.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == item.getCatId())
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, entitiesAll));
                    return categoryEntity;
                })
                .sorted(Comparator.comparingInt(categoryEntity ->
                        (categoryEntity.getSort() == null ? 0 : categoryEntity.getSort())))
                .collect(Collectors.toList());
        return collect;
    }

    @Override
    public void removeCategoryByIds(List<Long> catIds) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        baseMapper.deleteBatchIds(catIds);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        // 更新category
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //[2, 34, 225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return parentPath.toArray(new Long[parentPath.size()]);
    }

    // 225, 34, 2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }
}
