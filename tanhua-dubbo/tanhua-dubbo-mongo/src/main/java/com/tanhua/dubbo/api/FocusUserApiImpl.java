package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;

@DubboService
public class FocusUserApiImpl implements FocusUserApi{

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void save(FocusUser focusUser) {
        this.mongoTemplate.save(focusUser);
    }

    @Override
    public void delete(FocusUser focusUser) {
        Query query = new Query(
                Criteria.where("followUserId").is(focusUser.getFollowUserId())
                        .and("userId").is(focusUser.getUserId())
        );
        this.mongoTemplate.remove(query, FocusUser.class);
    }
}
