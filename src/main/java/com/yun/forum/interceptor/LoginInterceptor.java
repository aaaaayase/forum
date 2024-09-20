package com.yun.forum.interceptor;

import com.yun.forum.config.AppConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author yun
 * @date 2024/9/16 19:35
 * @desciption: 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${panda-forum.login.url}")
    private String defaultUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取session
        HttpSession session = request.getSession();
        // 校验是否登录
        if (session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
            return true;
        }
        // 未登录跳转页面
        if (!defaultUrl.startsWith("/")) {
            defaultUrl = "/" + defaultUrl;
        }
        // 校验不通过中断流程
        response.sendRedirect(defaultUrl);
        return false;
    }
}
