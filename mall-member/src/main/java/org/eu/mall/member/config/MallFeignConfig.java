package org.eu.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class MallFeignConfig {

    /**
     * 处理feign远程调用丢失请求头问题
     * @return
     */
    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor(){
            @Override
            public void apply(RequestTemplate template) {
                //1、RequestContextHolder拿到刚进来的这个请求
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes!=null){
                    System.out.println("RequestInterceptor线程...." + Thread.currentThread().getId());
                    HttpServletRequest request = attributes.getRequest(); //老请求
                    if(request != null){
                        //同步请求头数据，Cookie
                        String cookie = request.getHeader("Cookie");
                        //给新请求同步了老请求的cookie
                        template.header("Cookie",cookie);
                    }
                }
            }
        };
    }
}
