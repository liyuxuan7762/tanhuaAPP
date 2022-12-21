package com.tanhua.server.controller;

import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.service.UserInfoService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 新用户登录后完善个人信息
     * @param userInfo
     * @param token
     * @return
     */
    @PostMapping("/loginReginfo")
    public ResponseEntity loginReginfo(@RequestBody UserInfo userInfo,
                                       @RequestHeader("Authorization") String token) {
        // 1. 判断token是否合法 如果不合法抛出异常
        boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken) {
            throw new RuntimeException("用户未登录");
        }
        // 2. 从token中获取到id
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        // 3. 将ID设置到userInfo中
        userInfo.setId(Long.valueOf(id));
        // 4. 调用service方法保存数据
        this.userInfoService.save(userInfo);
        // 5. 返回结果
        return ResponseEntity.ok(null);
    }

    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto,
                               @RequestHeader("Authorization") String token) {
        // 1. 判断token是否合法 如果不合法抛出异常
        boolean verifyToken = JwtUtils.verifyToken(token);
        if (!verifyToken) {
            throw new RuntimeException("用户未登录");
        }
        // 2. 从token中获取到id
        Claims claims = JwtUtils.getClaims(token);
        Integer id = (Integer) claims.get("id");
        // 3. 调用service将头像上传并进行人像判断
        this.userInfoService.uploadAvatar(id, headPhoto);
        // 4. 返回结果
        return ResponseEntity.ok(null);
    }
}
