package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("tanhua.green")
public class GreenProperties {
    /**
     * 账号
     */
    String accessKeyID;
    /**
     * 密钥
     */
    String accessKeySecret;

    /**
     * 场景
     */
    String scenes;
}