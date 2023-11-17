package org.eu.mall.product.dao;

import org.apache.ibatis.annotations.Param;
import org.eu.mall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-27 00:34:58
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
