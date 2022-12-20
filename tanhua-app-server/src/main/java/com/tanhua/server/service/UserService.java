package com.tanhua.server.service;

import com.tanhua.autoconfig.template.EmailTemplate;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private EmailTemplate emailTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void sendMsg(String phone) {
        // 1. 生成验证码
        String code = RandomStringUtils.randomNumeric(6);
        // 2.调用发送验证码的方法
        emailTemplate.sendCode(phone, code);
        // 3.将验证码存入redis
        redisTemplate.opsForValue().set("CHECK_CODE:" + phone, code);
    }
}
