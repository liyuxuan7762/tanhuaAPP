package com.tanhua.server.service;

import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    public TodayBest getTodayBest() {
        // 1. 获取当前用户
        Long userId = UserHolder.getUserId();
        // 2. 调用api查询
        RecommendUser recommendUser = this.recommendUserApi.getTodayBest(userId);
        // 如果佳人不存在，则设置一个默认值
        if (recommendUser == null) {
            recommendUser = new RecommendUser();
            recommendUser.setUserId(1L);
            recommendUser.setScore(100.0);
        }
        // 3. 将返回数据封装成vo对象
        UserInfo userInfo = this.userInfoApi.getUserInfoById(recommendUser.getUserId());
        return TodayBest.init(userInfo, recommendUser);

    }
}
