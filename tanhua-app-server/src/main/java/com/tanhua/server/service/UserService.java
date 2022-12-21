package com.tanhua.server.service;

import com.tanhua.autoconfig.template.EmailTemplate;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.tanhua.common.RedisConstants.VERIFICATION_CODE_PREFIX;

@Service
public class UserService {

    @Resource
    private EmailTemplate emailTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @DubboReference
    private UserApi userApi;

    public void sendMsg(String phone) {
        // 1. 生成验证码
        // String code = RandomStringUtils.randomNumeric(6);
        // 2.调用发送验证码的方法
        // emailTemplate.sendCode(phone, code);
        // 3.将验证码存入redis
        String code = "123456"; // 方便测试将验证码写死
        redisTemplate.opsForValue().set(VERIFICATION_CODE_PREFIX + phone, code, Duration.ofMinutes(5));
    }

    public Map loginVerification(String phone, String code) {
        // 1.从Redis中获取到验证码
        String redisCode = this.redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + phone);
        // 2.比较验证码
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            // 验证码无效或者验证码错误
            throw new BusinessException(ErrorResult.loginError());
        }
        // 3.判断用户是否已经存在
        User user = userApi.findByMobile(phone);
        // 4.如果不存在则新建用户
        boolean isNew = false;
        if (user == null) {
            // 用户不存在
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long id = this.userApi.save(user);
            user.setId(id);
            isNew = true;
        }
        // 5.生成Token 保存id和phone
        Map tokenMap = new HashMap();
        tokenMap.put("id", user.getId());
        tokenMap.put("mobile", phone);
        String token = JwtUtils.getToken(tokenMap);
        // 6.封装结果
        Map retMap = new HashMap();
        retMap.put("token", token);
        retMap.put("isNew", isNew);
        return retMap;
    }
}
