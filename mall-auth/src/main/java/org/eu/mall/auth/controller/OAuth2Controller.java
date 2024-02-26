package org.eu.mall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.eu.common.constant.AuthConstant;
import org.eu.common.utils.HttpUtils;
import org.eu.common.utils.R;
import org.eu.common.vo.MemberRespVo;
import org.eu.mall.auth.feign.MemberFeignService;
import org.eu.mall.auth.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class OAuth2Controller {
    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletRequest request,  HttpServletResponse servletResponse) throws Exception {
        Map<String, String> header = new HashMap<>();
        Map<String, String> query = new HashMap<>();

        Map<String, String> map = new HashMap<>();
        map.put("client_id", "");
        map.put("client_secret", "");
        map.put("grant_type", "authorization_code");
        map.put("redirect_url", "http://auth.vmake.eu.org/oauth2.0/weibo/success");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", header, query, map);
        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            R oauthlogin = memberFeignService.oauthlogin(socialUser);
            if (oauthlogin.getCode() == 0) {
                MemberRespVo data = oauthlogin.getData("data", new TypeReference<MemberRespVo>() {
                });
                log.info("登录成功, 用户:{}", data.toString());
                session.setAttribute(AuthConstant.LOGIN_USER, data);
                return "redirect:http://www.vmake.eu.org";
            }else {
                return "redirect:http://auth.vmake.eu.org/login.html";
            }
        }else {
            return "redirect:http://auth.vmake.eu.org/login.html";
        }
    }
}
