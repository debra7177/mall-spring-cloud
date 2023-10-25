package org.eu.mall.ware.dao;

import org.eu.mall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 19:04:03
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
