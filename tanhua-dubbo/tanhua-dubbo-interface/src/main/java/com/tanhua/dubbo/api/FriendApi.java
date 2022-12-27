package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Friend;

import java.util.List;

public interface FriendApi {
    void save(Long userId, Long userId1);

    List<Friend> getFriendList(Long userId);
}
