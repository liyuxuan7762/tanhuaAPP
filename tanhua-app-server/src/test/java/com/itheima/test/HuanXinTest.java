package com.itheima.test;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import org.junit.Before;
import org.junit.Test;

public class HuanXinTest {
    private EMService service;
    @Before
    public void init() {
        EMProperties cliProperties;
        EMProperties properties = EMProperties.builder()
                .setAppkey("1173221227168624#tanhua")
                .setClientId("YXA6FTjswNWvQ0CUku4I5tAnLQ")
                .setClientSecret("YXA6wcugZzJFxYE8zNr0svKhsraH27k")
                .build();
        service = new EMService(properties);
    }

    @Test
    public void testSignUp() {
        // 测试注册一个用户到环信
        // service.user().create("hx002", "12345").block();
        service.user().create("hx002", "12345").block();
    }

    @Test
    public void setRelation() {
        // 设置好友关系
        service.contact().add("hx001", "hx002");
    }
}
