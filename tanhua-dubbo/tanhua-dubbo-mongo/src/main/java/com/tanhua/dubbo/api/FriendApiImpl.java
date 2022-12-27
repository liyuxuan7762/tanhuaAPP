package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class FriendApiImpl implements FriendApi {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Long userId, Long userId1) {
        saveFriend(userId, userId1);
        saveFriend(userId1, userId);
    }

    @Override
    public List<Friend> getFriendList(Long userId) {
        Query query = new Query(Criteria.where("userId").is(userId));
        return this.mongoTemplate.find(query, Friend.class);
    }

    private void saveFriend(Long userId, Long userId1) {
        Query query = new Query(
                Criteria.where("userId").is(userId).
                        and("friendId").is(userId1)
        );
        boolean flag = this.mongoTemplate.exists(query, Friend.class);
        if (!flag) {
            Friend friend = new Friend();
            friend.setUserId(userId);
            friend.setFriendId(userId1);
            friend.setCreated(System.currentTimeMillis());
            this.mongoTemplate.save(friend);
        }
    }
}
