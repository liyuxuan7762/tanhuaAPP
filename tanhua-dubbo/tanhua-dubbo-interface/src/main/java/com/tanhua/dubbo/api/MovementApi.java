package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface MovementApi {
    void publish(Movement movement);

    List<Friend> getFriendListByUserId(Long userId);

    void saveTimeLine(Movement movement, long createTime);

    PageResult getMovementByUserId(Long userId, Integer page, Integer pagesize);
}