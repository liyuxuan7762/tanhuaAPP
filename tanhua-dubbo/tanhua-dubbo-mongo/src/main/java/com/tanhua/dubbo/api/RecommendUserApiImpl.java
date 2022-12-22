package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser getTodayBest(Long userId) {
        // 根据toUserId字段查询，然后根据分数排序，取分数最高的一个
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = new Query().with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }
}
