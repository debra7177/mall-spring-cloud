package org.eu.mall.member.dao;

import org.eu.mall.member.entity.MemberCollectSpuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员收藏的商品
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:48:06
 */
@Mapper
public interface MemberCollectSpuDao extends BaseMapper<MemberCollectSpuEntity> {
	
}
