package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.UserLike;

import java.util.List;
import java.util.Map;

public interface UserLikeApi {
    boolean save(Long userId, Long userId1, boolean b);

    List<UserLike> getUserLikeByUserId(Long userId);

    Map<Long, UserLike> getUserLikeByUserLikeId(Long userId);
}
