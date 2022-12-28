package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface RecommendUserApi {
    RecommendUser getTodayBest(Long userId);

    PageResult getRecommendationList(Integer page, Integer pagesize, Long userId);

    RecommendUser getRecommendUserByUserId(Long userId);

    List<RecommendUser> getCards(Long userId, int i);
}
