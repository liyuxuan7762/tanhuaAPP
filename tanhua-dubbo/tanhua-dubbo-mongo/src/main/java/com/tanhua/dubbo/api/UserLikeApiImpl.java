package com.tanhua.dubbo.api;


import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.UserLike;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@DubboService
public class UserLikeApiImpl implements UserLikeApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean save(Long userId, Long likeUserId, boolean isLike) {
        Query query = new Query(
                Criteria.where("userId").is(userId)
                        .and("likeUserId").is(likeUserId)
        );
        UserLike userLike = this.mongoTemplate.findOne(query, UserLike.class);
        try {
            if (userLike == null) {
                // 不存在
                userLike = new UserLike();
                userLike.setUserId(userId);
                userLike.setLikeUserId(likeUserId);
                userLike.setIsLike(isLike);
                userLike.setCreated(System.currentTimeMillis());
                userLike.setUpdated(System.currentTimeMillis());
                this.mongoTemplate.save(userLike);
            } else {
                // 存在
                Update update = new Update();
                update.set("isLike", isLike);
                update.set("updated", System.currentTimeMillis());
                this.mongoTemplate.updateFirst(query, update, UserLike.class);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<UserLike> getUserLikeByUserId(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.find(query, UserLike.class);
    }

    @Override
    public Map<Long, UserLike> getUserLikeByUserLikeId(Long userId) {
        Query query = new Query(Criteria.where("likeUserId").is(userId));
        List<UserLike> userLikeList = this.mongoTemplate.find(query, UserLike.class);
        return CollUtil.fieldValueMap(userLikeList, "userId");
    }
}
