package com.tanhua.autoconfig.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "tanhua.huanxin")
public class HuanXinProperties {
    private String appKey;
    private String ClientId;
    private String secretKey;
}
