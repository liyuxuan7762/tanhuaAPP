package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.face")
public class AipFaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;

}
