package com.tanhua.server.controller;

import com.tanhua.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        String phone = map.get("phone").toString();
        this.userService.sendMsg(phone);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/loginVerification")
    public ResponseEntity loginVerification(@RequestBody Map map) {
        // 1.解析参数
        String phone = map.get("phone").toString();
        String code = map.get("verificationCode").toString();
        // 2.调用Service方法
        Map retMap = this.userService.loginVerification(phone, code);
        // 3.返回结果
        return ResponseEntity.ok(retMap);
    }


}
