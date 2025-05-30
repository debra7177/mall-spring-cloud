package org.eu.mall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 1、spring-session依赖，
 * 2、spring-session配置
 * 3、引入LoginInterceptor、WebMvcConfigure
 */
@Configuration
public class MallSessionConfig {
    // cookie自定义作用域
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setCookieName("MALLSESSION");
        cookieSerializer.setDomainName("vmake.eu.org");
        return cookieSerializer;
    }

    // session以JSON格式存储
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
