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

}
