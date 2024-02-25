package org.eu.mall.auth.feign;

import org.eu.common.utils.R;
import org.eu.mall.auth.vo.SocialUser;
import org.eu.mall.auth.vo.UserLoginVo;
import org.eu.mall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mall-member")
public interface MemberFeignService {
    // 注册用户
    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegistVo vo);

    // 登录
    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthlogin(@RequestBody SocialUser socialUser) throws Exception;
}
