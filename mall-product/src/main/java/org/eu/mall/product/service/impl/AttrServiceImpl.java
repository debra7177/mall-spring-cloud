package org.eu.mall.product.service.impl;

import org.eu.common.constant.ProductConstant;
import org.eu.mall.product.entity.AttrAttrgroupRelationEntity;
import org.eu.mall.product.entity.AttrGroupEntity;
import org.eu.mall.product.entity.CategoryEntity;
import org.eu.mall.product.service.AttrAttrgroupRelationService;
import org.eu.mall.product.service.AttrGroupService;
import org.eu.mall.product.service.CategoryService;
import org.eu.mall.product.vo.AttrGroupRelationVo;
import org.eu.mall.product.vo.AttrResponseVo;
import org.eu.mall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.product.dao.AttrDao;
import org.eu.mall.product.entity.AttrEntity;
import org.eu.mall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 保存基本数据
        this.save(attrEntity);
        // 保存属性 - 属性分组 关联关系
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId() != null) {
            // 基本属性
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());

            attrAttrgroupRelationService.save(relationEntity);
        }

    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(type) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and(wrapper -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrResponseVo> collect = page.getRecords().stream().map(item -> {
            AttrResponseVo attrResponseVo = new AttrResponseVo();
            BeanUtils.copyProperties(item, attrResponseVo);
            // 设置所属三级分类名称
            //CategoryEntity categoryEntity = categoryService.getOne(new QueryWrapper<CategoryEntity>().eq("cat_id", item.getCatelogId()));
            CategoryEntity categoryEntity = categoryService.getById(item.getCatelogId());
            attrResponseVo.setCatelogName(categoryEntity.getName());

            // 设置所属属性分组名称
            if ("base".equalsIgnoreCase(type)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", item.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }


            return attrResponseVo;
        }).collect(Collectors.toList());
        pageUtils.setList(collect);

        return pageUtils;
    }

    @Override
    public AttrResponseVo getAttrInfo(Long attrId) {
        AttrResponseVo attrResponseVo = new AttrResponseVo();
        AttrEntity attrEntity = this.getById(attrId);

        BeanUtils.copyProperties(attrEntity, attrResponseVo);

        // 设置属性分组名称
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (relationEntity != null) {
                attrResponseVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrResponseVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
        }

        // 设置分类信息
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrResponseVo.setCatelogPath(catelogPath);

        CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrResponseVo.setCatelogName(categoryEntity.getName());
        }

        return attrResponseVo;
    }

    // 更新规格参数
    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);

        this.updateById(attrEntity);

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 更新属性 - 属性分组关联关系
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());

            int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationService.update(relationEntity, new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            }else {
                attrAttrgroupRelationService.save(relationEntity);
            }
        }
    }

    /**
     * 根据 属性分组id 查询关联是所有基本属性
     * @param attrGroupId 属性分组id
     * @return 基本属性列表
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        if (relationEntities != null && relationEntities.size() > 0) {
            // 批量获取关系列表
            List<Long> attrIdList = relationEntities.stream()
                    .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            return (List<AttrEntity>) listByIds(attrIdList);
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        if (vos.length > 0) {
            for (AttrGroupRelationVo vo : vos) {
                attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", vo.getAttrId()).eq("attr_group_id", vo.getAttrGroupId()));
            }
        }
        List<AttrAttrgroupRelationEntity> relationEntityList = Arrays.asList(vos).stream()
                .map(item -> {
                    AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
                    BeanUtils.copyProperties(item, relationEntity);
                    return relationEntity;
                }).collect(Collectors.toList());
        attrAttrgroupRelationService.deleteBatchRelation(relationEntityList);
    }

    /**
     * 获取当前属性分组没有关联的所有属性
     * @param params
     * @param attrGroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrGroupId) {
        // 当前属性分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 2.当前属性分组只能关联别的分组没有引用的属性
        // 2.1 查询当前分类下的所有属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIdList = attrGroupEntities.stream()
                .map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());
        // 2.2 这些属性分组关联的属性(已经关联 需要排除attrIdSet)
        List<AttrAttrgroupRelationEntity> entityList = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIdList));
        Set<Long> attrIdSet = entityList.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toSet());
        // 2.3 从当前分类的所有属性中移除这些已经被关联过的属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIdSet.size() > 0) {
            queryWrapper.notIn("attr_id", attrIdSet);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((w)->{
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        return new PageUtils(page);
    }

    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }

}
