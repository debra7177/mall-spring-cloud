package org.eu.mall.product.service.impl;

import org.eu.mall.product.dao.BrandDao;
import org.eu.mall.product.dao.CategoryDao;
import org.eu.mall.product.entity.BrandEntity;
import org.eu.mall.product.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.product.dao.CategoryBrandRelationDao;
import org.eu.mall.product.entity.CategoryBrandRelationEntity;
import org.eu.mall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        BrandEntity brandEntity = brandDao.selectById(brandId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        save(categoryBrandRelation);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        baseMapper.updateCategory(catId, name);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandName(name);
        update(categoryBrandRelationEntity, new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = list(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<Long> brandIds = categoryBrandRelationEntities.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());
        if (brandIds.size() > 0) {
            return brandDao.selectBatchIds(brandIds);
        }
        return null;
    }
}
