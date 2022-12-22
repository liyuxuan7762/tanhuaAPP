package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;

public interface RecommendUserApi {
    RecommendUser getTodayBest(Long userId);
}
