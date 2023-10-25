package org.eu.mall.ware.dao;

import org.eu.mall.ware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 19:04:03
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {
	
}
