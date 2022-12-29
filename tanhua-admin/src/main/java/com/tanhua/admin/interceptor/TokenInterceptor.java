package com.tanhua.admin.interceptor;

import com.tanhua.admin.service.AdminService;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义拦截器
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private AdminService adminService;

    /**
     * 前置处理
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、获取头信息
        String token = request.getHeader("Authorization");
        token = token.replace("Bearer ", "");

        //2、调用service根据token查询用户
        Claims claims = JwtUtils.getClaims(token);
        Admin admin = new Admin();
        admin.setId(claims.get("id",Long.class));
        admin.setUsername(claims.get("username",String.class));
        //4、将对象存入Threadlocal
        AdminHolder.set(admin);
        return true;
    }
}
