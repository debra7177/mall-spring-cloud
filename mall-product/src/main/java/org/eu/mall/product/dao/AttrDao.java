package org.eu.mall.product.dao;

import org.eu.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 05:32:12
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
