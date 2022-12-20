package com.itheima.test;

import com.tanhua.autoconfig.template.EmailTemplate;
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

    @Autowired
    private EmailTemplate emailTemplate;

    //测试
    @Test
    public void testSendSms() {
        emailTemplate.sendCode("776239423@qq.com", "1234");
    }
}
