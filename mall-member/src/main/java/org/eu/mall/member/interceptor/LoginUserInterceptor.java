package org.eu.mall.member.interceptor;

import org.eu.common.constant.AuthConstant;
import org.eu.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 /member
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match) {
            return true;
        }
        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthConstant.LOGIN_USER);
        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        }
        // 没有登录
        request.getSession().setAttribute("msg", "请先登录");
        response.sendRedirect("http://auth.vmake.eu.org/login.html");
        return false;
    }
}
