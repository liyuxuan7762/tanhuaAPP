package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import com.tanhua.common.utils.Constants;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class UserFreezeService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void checkUser(String status, Long userId) {
        // 1. 根据用户id构建key
        String key = Constants.USER_FREEZE + userId;
        // 2. 到redis中查询key
        String value = this.redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(value)) {
            // 3. 解析key的内容，判断冻结的条件
            Map map = JSON.parseObject(value, Map.class);
            // 从Map中获取到冻结的范围
            String freezingRange = map.get("freezingRange").toString();
            if (status.equals(freezingRange)) {
                throw new BusinessException(ErrorResult.builder().errMessage("用户被冻结").build());
            }
        }

    }
}
