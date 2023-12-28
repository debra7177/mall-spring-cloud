package org.eu.mall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.eu.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.eu.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;

/**
 * 属性分组
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-27 00:34:58
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);
}
