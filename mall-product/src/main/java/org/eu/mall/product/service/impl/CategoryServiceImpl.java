package org.eu.mall.product.service.impl;

import org.eu.mall.product.service.CategoryBrandRelationService;
import org.eu.mall.product.vo.Catelog2Vo;
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

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        // 查询所有一级分类
        List<CategoryEntity> level1Categorys = getLevel1Categorys();
        // 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 查询二级分类
            List<CategoryEntity> categoryEntities = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 将当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = list(new QueryWrapper<CategoryEntity>().eq("parent_cid", l2.getCatId()));
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_cid;
    }
}
