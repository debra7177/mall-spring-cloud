package org.eu.mall.coupon.dao;

import org.eu.mall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:42:21
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
