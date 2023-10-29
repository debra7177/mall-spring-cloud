package org.eu.mall.product.service.impl;

import org.eu.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.eu.common.utils.PageUtils;
import org.eu.common.utils.Query;

import org.eu.mall.product.dao.AttrAttrgroupRelationDao;
import org.eu.mall.product.entity.AttrAttrgroupRelationEntity;
import org.eu.mall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteBatchRelation(List<AttrAttrgroupRelationEntity> relationEntityList) {
        baseMapper.deleteBatchRelation(relationEntityList);
    }

    @Override
    public void saveBatch(List<AttrGroupRelationVo> vos) {
        if (vos.size() > 0) {
            List<AttrAttrgroupRelationEntity> relationEntities = vos.stream().map(item -> {
                AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
                BeanUtils.copyProperties(item, relationEntity);
                return relationEntity;
            }).collect(Collectors.toList());
            saveBatch(relationEntities);
        }
    }
}
