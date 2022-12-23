package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementTimeLine;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class MovementApiImpl implements MovementApi {

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private IdWorker idWorker;

    @Override
    public void publish(Movement movement) {
        try {
            // 1. 设置pid
            Long pid = idWorker.getNextId("movement");
            movement.setPid(pid);
            // 2. 设置更新时间
            long createTime = System.currentTimeMillis();
            movement.setCreated(createTime);
            // 3. 将动态写入动态表
            this.mongoTemplate.save(movement);
            // 4. 根据用户Id查找对应的好友id 这里直接查找了用户的所有好友，并没有考虑用户会设置动态对指定好友不可见的功能
            // 以异步的方式去写入时间写表
            saveTimeLine(movement, createTime);
        } catch (Exception e) {
            // 保证事务
            throw new RuntimeException(e);
        }

    }

    /**
     * 异步将动态信息写入到TimeLine中
     *
     * @param movement
     * @param createTime
     */
    @Async
    @Override
    public void saveTimeLine(Movement movement, long createTime) {
        List<Friend> friendList = this.getFriendListByUserId(movement.getUserId());
        MovementTimeLine timeLine = null;
        if (!friendList.isEmpty()) {
            for (Friend friend : friendList) {
                // 封装TimeLine对象，保存到TimeLine表中
                timeLine = new MovementTimeLine();
                timeLine.setCreated(createTime);
                timeLine.setUserId(movement.getUserId());
                timeLine.setMovementId(movement.getId());
                timeLine.setFriendId(friend.getFriendId());
                // 将记录保存TimeLine表
                this.mongoTemplate.save(timeLine);
            }
        }
    }

    @Override
    public PageResult getMovementByUserId(Long userId, Integer page, Integer pagesize) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        List<Movement> movementList = this.mongoTemplate.find(query, Movement.class);
        return new PageResult(page, pagesize, 0L, movementList);
    }

    @Override
    public List<Friend> getFriendListByUserId(Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        return this.mongoTemplate.find(query, Friend.class);
    }


}
