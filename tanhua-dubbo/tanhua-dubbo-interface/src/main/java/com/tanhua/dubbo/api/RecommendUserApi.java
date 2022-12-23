package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

public interface RecommendUserApi {
    RecommendUser getTodayBest(Long userId);

    PageResult getRecommendationList(Integer page, Integer pagesize, Long userId);
}
