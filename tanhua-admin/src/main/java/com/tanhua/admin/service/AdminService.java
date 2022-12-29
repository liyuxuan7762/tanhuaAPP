package com.tanhua.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.admin.exception.BusinessException;
import com.tanhua.admin.interceptor.AdminHolder;
import com.tanhua.admin.mapper.AdminMapper;
import com.tanhua.common.utils.Constants;
import com.tanhua.commons.utils.JwtUtils;
import com.tanhua.model.domain.Admin;
import com.tanhua.model.vo.AdminVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public Map login(Map map) {
        // 获取参数
        String username = map.get("username").toString();
        String password = map.get("password").toString();
        String verificationCode = map.get("verificationCode").toString();
        String uuid = map.get("uuid").toString();

        // 验证验证码
        String redisCode = this.redisTemplate.opsForValue().get(Constants.CAP_CODE + uuid);
        if (StringUtils.isEmpty(redisCode) || !redisCode.equals(verificationCode)) {
            throw new BusinessException("验证码错误");
        }

        // 验证账号密码
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        queryWrapper.eq(Admin::getPassword, password);
        Admin admin = this.adminMapper.selectOne(queryWrapper);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误！");
        }

        // 生成token
        Map tokenMap = new HashMap();
        tokenMap.put("id", admin.getId());
        tokenMap.put("username", admin.getUsername());
        String token = JwtUtils.getToken(map);

        // 封装结果
        Map retMap = new HashMap();
        retMap.put("token", token);
        return retMap;

    }

    public AdminVo profile() {
        Long userId = AdminHolder.getUserId();
        Admin admin = this.adminMapper.selectById(userId);
        return AdminVo.init(admin);
    }
}
