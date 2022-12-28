package com.tanhua.server.controller;


import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 根据id查询用户的详细信息
     * @param userID
     * @param token
     * @return
     */
    @GetMapping
    public ResponseEntity getUserInfoById(Long userID) {
        // 3. 判断ID是否为空
        if (userID == null) {
            // 查询当前用户的资料
            userID = UserHolder.getUserId();
        }
        // 4. 调用service方法查询用户信息
        UserInfoVo userInfoVo = this.userInfoService.getUserInfoById(userID);

        return ResponseEntity.ok(userInfoVo);
    }

    /**
     * 用户在我的界面中修改自己的个人信息
     * @param userInfo
     * @param token
     * @return
     */
    @PutMapping
    public ResponseEntity updateUserInfo(@RequestBody UserInfo userInfo, @RequestHeader("Authorization") String token) {
        // 3. 设置userId
        userInfo.setId(UserHolder.getUserId());

        // 4. 调用service方法 更新用户信息
        this.userInfoService.updateUserInfo(userInfo);

        // 5. 返回响应信息
        return ResponseEntity.ok(null);
    }

    /**
     * 更新头像
     * @param headPhoto
     * @return
     */
    @PostMapping("/header")
    public ResponseEntity updateHeader(MultipartFile headPhoto) {
        this.userInfoService.updateHeader(headPhoto);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/counts")
    public ResponseEntity getCount() {
        this.
    }
}
