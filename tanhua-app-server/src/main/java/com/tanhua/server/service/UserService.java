package com.tanhua.server.service;

import com.tanhua.autoconfig.template.EmailTemplate;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.common.utils.LogOperationCodeConstants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
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

    @Resource
    private HuanXinTemplate huanXinTemplate;

    @Resource
    private UserFreezeService userFreezeService;

    @Resource
    private MqMessageService messageService;

    public void sendMsg(String phone) {

        User user = this.userApi.findByMobile(phone);
        if (user != null) {
            userFreezeService.checkUser("1", user.getId());
        }

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
        String type = LogOperationCodeConstants.LOGIN;
        if (user == null) {
            type = LogOperationCodeConstants.SIGN_UP;
            // 用户不存在
            user = new User();
            user.setMobile(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            Long id = this.userApi.save(user);
            user.setId(id);
            isNew = true;

            // 将用户注册到环信
            // 1. 生成环信的用户名和密码
            String hxUser = Constants.HX_USER_PREFIX + user.getId();
            // 2. 保存到环信
            Boolean flag = this.huanXinTemplate.createUser(hxUser, Constants.INIT_PASSWORD);
            // 3. 如果保存成功，则将用户名密码保存到数据库中
            if (flag) {
                user.setHxUser(hxUser);
                user.setHxPassword(Constants.INIT_PASSWORD);
                this.userApi.updateHx(user);
            }
        }

        this.messageService.sendLogService(UserHolder.getUserId(), type, LogOperationCodeConstants.MESSAGE_USER_KEY, null);

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

    public boolean checkVerificationCode(String code, String userPhone) {
        // 1.从Redis中获取到验证码
        String redisCode = this.redisTemplate.opsForValue().get(VERIFICATION_CODE_PREFIX + userPhone);
        // 2.比较验证码
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(code)) {
            // 验证码无效或者验证码错误
            throw new BusinessException(ErrorResult.loginError());
        }
        return true;
    }

    public void updatePhone(String phone, Long userId) {
        this.userApi.updatePhone(phone, userId);
    }
}
