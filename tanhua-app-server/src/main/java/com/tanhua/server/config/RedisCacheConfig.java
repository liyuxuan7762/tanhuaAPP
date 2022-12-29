package com.tanhua.server.config;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    //设置失效时间
    private static final Map<String, Duration> cacheMap;

    static {
        cacheMap = ImmutableMap.<String, Duration>builder().put("videos", Duration.ofSeconds(30L)).build();
    }

    //配置RedisCacheManagerBuilderCustomizer对象
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> {
            //根据不同的cachename设置不同的失效时间
            for (Map.Entry<String, Duration> entry : cacheMap.entrySet()) {
                builder.withCacheConfiguration(entry.getKey(),
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(entry.getValue()));
            }
        };
    }
}
