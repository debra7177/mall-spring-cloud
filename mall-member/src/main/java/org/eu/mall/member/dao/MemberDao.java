package org.eu.mall.member.dao;

import org.eu.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:48:06
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
