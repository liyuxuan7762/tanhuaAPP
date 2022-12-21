package com.tanhua.server.controller;

import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
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
        // 3. 将ID设置到userInfo中
        userInfo.setId(UserHolder.getUserId());
        // 4. 调用service方法保存数据
        this.userInfoService.save(userInfo);
        // 5. 返回结果
        return ResponseEntity.ok(null);
    }

    @PostMapping("/loginReginfo/head")
    public ResponseEntity head(MultipartFile headPhoto,
                               @RequestHeader("Authorization") String token) {

        // 3. 调用service将头像上传并进行人像判断
        this.userInfoService.uploadAvatar(UserHolder.getUserId(), headPhoto);
        // 4. 返回结果
        return ResponseEntity.ok(null);
    }
}
