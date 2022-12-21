package com.tanhua.autoconfig;


import com.tanhua.autoconfig.properties.AipFaceProperties;
import com.tanhua.autoconfig.properties.EmailProperties;
import com.tanhua.autoconfig.properties.OssProperties;
import com.tanhua.autoconfig.properties.SmsProperties;
import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.EmailTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.autoconfig.template.SmsTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({
        SmsProperties.class,
        EmailProperties.class,
        OssProperties.class,
        AipFaceProperties.class
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
}
