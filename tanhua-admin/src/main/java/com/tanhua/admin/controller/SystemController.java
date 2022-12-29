package com.tanhua.admin.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.tanhua.admin.service.AdminService;
import com.tanhua.common.utils.Constants;
import com.tanhua.model.vo.AdminVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/system/users")
public class SystemController {

    @Resource
    private AdminService adminService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码图片
     *
     * @param uuid
     * @param response
     * @throws IOException
     */
    @GetMapping("/verification")
    public void verificationCode(String uuid, HttpServletResponse response) throws IOException {
        // 获取验证码
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(299, 97);
        // 保存到Redis
        this.redisTemplate.opsForValue().set(Constants.CAP_CODE + uuid, lineCaptcha.getCode());
        // 将图片写入前端
        lineCaptcha.write(response.getOutputStream());
    }

    /**
     * 登录获取token
     *
     * @param map
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map map) {
        Map retMap = this.adminService.login(map);
        return ResponseEntity.ok(map);
    }

    @PostMapping("/profile")
    public ResponseEntity profile() {
        AdminVo vo = this.adminService.profile();
        return ResponseEntity.ok(vo);
    }

}
