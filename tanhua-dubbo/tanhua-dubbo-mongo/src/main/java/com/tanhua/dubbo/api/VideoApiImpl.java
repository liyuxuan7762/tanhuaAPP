package com.tanhua.dubbo.api;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;

@DubboService
public class VideoApiImpl implements VideoApi {
    @Resource
    private MongoTemplate mongoTemplate;
}
