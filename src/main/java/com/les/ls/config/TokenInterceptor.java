package com.les.ls.config;

import cn.hutool.core.util.StrUtil;
import com.les.ls.exception.TokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private static final String TOKEN_KEY = "token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //登录请求放开
        String uri = request.getRequestURI();
        if (uri.equals("/login")) {
            return true;
        }
        // 尝试从请求头从获取token
        String token = request.getHeader(TOKEN_KEY);
        if (StrUtil.isEmpty(token)) {
            // 尝试从请求参数中获取token
            token = request.getParameter(token);
        }

        if (StrUtil.isEmpty(token)) {
            throw new TokenException("token check failed !");
        }
        return true;
    }
}
