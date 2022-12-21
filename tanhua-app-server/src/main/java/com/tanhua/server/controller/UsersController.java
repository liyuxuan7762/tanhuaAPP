package com.tanhua.server.controller;


import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Resource
    private UserInfoService userInfoService;

    @GetMapping
    public ResponseEntity getUserInfoById(Long userID, @RequestHeader("Authorization") String token) {
        // 3. 判断ID是否为空
        if (userID == null) {
            // 查询当前用户的资料
            userID = UserHolder.getUserId();
        }
        // 4. 调用service方法查询用户信息
        UserInfoVo userInfoVo = this.userInfoService.getUserInfoById(userID);

        return ResponseEntity.ok(userInfoVo);
    }

    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String token) {
        // 3. 设置userId
        userInfo.setId(UserHolder.getUserId());

        // 4. 调用service方法 更新用户信息
        this.userInfoService.updateUserInfo(userInfo);

        // 5. 返回响应信息
        return ResponseEntity.ok(null);
    }
}
