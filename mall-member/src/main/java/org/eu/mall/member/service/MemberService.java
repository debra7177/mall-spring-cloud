package org.eu.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.eu.common.utils.PageUtils;
import org.eu.mall.member.entity.MemberEntity;
import org.eu.mall.member.exception.PhoneExistException;
import org.eu.mall.member.exception.UsernameExistException;
import org.eu.mall.member.vo.MemberLoginVo;
import org.eu.mall.member.vo.MemberRegistVo;
import org.eu.mall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author kevin
 * @email drzhong2015@gmail.com
 * @date 2023-10-25 18:48:06
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 注册用户
     * @param vo
     */
    void regist(MemberRegistVo vo);

    /**
     * 检查邮箱唯一
     * @param phone
     * @return
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 检查用户名唯一
     * @param userName
     * @return
     */
    void checkUserNameUnique(String userName) throws UsernameExistException;

    MemberEntity login(MemberLoginVo vo);

    /**
     * 社交登录
     * @param socialUser
     * @return
     */
    MemberEntity login(SocialUser socialUser);
}

