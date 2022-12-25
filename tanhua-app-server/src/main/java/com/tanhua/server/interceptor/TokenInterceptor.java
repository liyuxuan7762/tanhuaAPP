package com.tanhua.server.interceptor;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.User;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取token
        String token = request.getHeader("Authorization");
        // String token = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2NzI1NTczNzksIm1vYmlsZSI6IjEzODAwMTM4MDAwIiwiaWQiOjEwNn0.2YegD_q_gQKjLln1LF9FHxvNLMDzvzvb9BZwKUEjVHkBsJhb9z9iNPsU22mTFjc5WCbNlwkHIPuyV5MSI-2mmw";
        // 2. 判断token是否合法 如果不合法 返回401错误
        boolean flag = JwtUtils.verifyToken(token);
        if (!flag) {
            // token不合法
            response.setStatus(401);
            return false;
        }
        // 3. 解析token，将用户信息保存到threadLocal
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        String mobile = claims.get("mobile").toString();
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(mobile);
        UserHolder.setUser(user);
        // 4. 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
