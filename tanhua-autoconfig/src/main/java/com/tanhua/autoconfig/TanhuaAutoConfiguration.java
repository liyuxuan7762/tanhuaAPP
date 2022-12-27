package com.tanhua.autoconfig;


import com.tanhua.autoconfig.properties.*;
import com.tanhua.autoconfig.template.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        EmailProperties.class,
        OssProperties.class,
        AipFaceProperties.class,
        HuanXinProperties.class
})
public class TanhuaAutoConfiguration {

    @Bean
    public SmsTemplate smsTemplate(SmsProperties properties) {
        return new SmsTemplate(properties);
    }

    @Bean
    public EmailTemplate emailTemplate(EmailProperties properties) {
        return new EmailTemplate(properties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties) {
        return new OssTemplate(ossProperties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate(AipFaceProperties aipFaceProperties) {
        return new AipFaceTemplate(aipFaceProperties);
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties) {
        return new HuanXinTemplate(huanXinProperties);
    }
}
