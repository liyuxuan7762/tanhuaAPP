package com.itheima.test;

import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static com.tanhua.common.utils.Constants.MOVEMENTS_RECOMMEND;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class RedisTest {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 向Redis中添加推荐动态测试数据
     */
    @Test
    public void addDataToRedis() {
        Long userId = 106L;
        redisTemplate.opsForValue().set(MOVEMENTS_RECOMMEND + userId, "10067,10015,10081");
        String s = redisTemplate.opsForValue().get(MOVEMENTS_RECOMMEND + userId);
        System.out.println(s);
    }

}
