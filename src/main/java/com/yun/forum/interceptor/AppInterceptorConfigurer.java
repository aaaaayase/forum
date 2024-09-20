package com.yun.forum.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author yun
 * @date 2024/9/16 19:39
 * @desciption: 登录拦截器配置
 */
@Configuration
public class AppInterceptorConfigurer implements WebMvcConfigurer {

    // 注入拦截器
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/logout")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/sign-in.html")
                .excludePathPatterns("/sign-up.html")
                .excludePathPatterns("/dist/**")
                .excludePathPatterns("/image/**")
                .excludePathPatterns("/js/**")
                .excludePathPatterns("/**.ico")
        ;
    }
}
