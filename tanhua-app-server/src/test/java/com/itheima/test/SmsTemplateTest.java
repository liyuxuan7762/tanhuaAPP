package com.itheima.test;

import com.tanhua.autoconfig.template.SmsTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class SmsTemplateTest {

    //注入
    @Autowired
    private SmsTemplate smsTemplate;

    //测试
    @Test
    public void testSendSms() {
        smsTemplate.sendSms("18618412321","4567");
    }
}
