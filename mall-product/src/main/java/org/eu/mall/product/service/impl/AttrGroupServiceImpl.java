package org.eu.mall.product.service.impl;

import org.eu.mall.product.entity.AttrEntity;
import org.eu.mall.product.service.AttrService;
import org.eu.mall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
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

import org.eu.mall.product.dao.AttrGroupDao;
import org.eu.mall.product.entity.AttrGroupEntity;
import org.eu.mall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


/**
 * The type Attr group service.
 */
@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, long cateLogId) {
        String key = (String) params.get("key");
        // select * from pms_attr_group where cateLog_id=? and attr_group_id=key or attr_group_name like %key%
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(obj -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (cateLogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
        wrapper.eq("catelog_id", cateLogId);
        IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    /**
     * 根据三级分类id查询 属性分组和属性(发布商品 - 规格参数步骤页面)
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsBycatelogId(Long catelogId) {
        List<AttrGroupEntity> attrGroupEntityList = list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        if (attrGroupEntityList != null && attrGroupEntityList.size() > 0) {
            return attrGroupEntityList.stream().map(item -> {
                AttrGroupWithAttrsVo attrGroupWithAttrsVo = new AttrGroupWithAttrsVo();
                BeanUtils.copyProperties(item, attrGroupWithAttrsVo);
                List<AttrEntity> attrEntities = attrService.getRelationAttr(item.getAttrGroupId());
                attrGroupWithAttrsVo.setAttrs(attrEntities);
                return attrGroupWithAttrsVo;
            }).collect(Collectors.toList());
        }
        return null;
    }
}
