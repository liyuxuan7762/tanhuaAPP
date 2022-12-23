package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.RecommendUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.RecommendUserDto;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public PageResult getRecommendationList(RecommendUserDto dto) {
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 根据用户id分页查询推荐列表
        PageResult result = this.recommendUserApi.getRecommendationList(dto.getPage(), dto.getPagesize(), userId);
        if (result.getItems() != null && result.getItems().size() > 0) {
            // 3. 根据推荐用户的ids查询所有推荐用户的信息
            List<RecommendUser> items = (List<RecommendUser>) result.getItems();
            List<TodayBest> todayBestList = new ArrayList<>();
            // 4. 循环遍历
//        if (items != null && items.size() > 0) {
//            for (RecommendUser item : items) {
//                Long recommendUserId = item.getUserId();
//                UserInfo recommendUserInfo = this.userInfoApi.getUserInfoById(recommendUserId);
//                // 判断条件是否满足 年龄 城市 学历 性别
//                // 性别
//                if (!StringUtils.isEmpty(dto.getGender()) && !dto.getGender().equals(recommendUserInfo.getGender())) {
//                    continue;
//                }
//                // 年龄
//                if (dto.getAge() != null && recommendUserInfo.getAge() >= dto.getAge()) {
//                    continue;
//                }
//                // 城市
//                if (!StringUtils.isEmpty(dto.getCity()) && !dto.getCity().equals(recommendUserInfo.getCity())) {
//                    continue;
//                }
//                // 学历
//                if (!StringUtils.isEmpty(dto.getEducation()) && !dto.getEducation().equals(recommendUserInfo.getEducation())) {
//                    continue;
//                }
//                // 满足条件，封装对象
//                todayBestList.add(TodayBest.init(recommendUserInfo, item));
//            }
//        }
            // 4. 构建返回结果

            // items中获取到所有推荐人的用户id
            List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);
            // 设置插叙条件
            UserInfo condition = new UserInfo();
            condition.setAge(dto.getAge());
            condition.setCity(dto.getCity());
            condition.setEducation(dto.getEducation());
            condition.setGender(dto.getGender());
            // 调用api方法，批量查询
            Map<Long, UserInfo> userInfoMap = this.userInfoApi.getUserInfoByIds(ids, condition);

            // 循环items，从中取出userid，根据userId从Map中取出数据，并封装成TodayBest的VO类
            for (RecommendUser item : items) {
                Long recommendUserId = item.getUserId();
                UserInfo userInfo = userInfoMap.get(recommendUserId);
                if (userInfo != null) {
                    todayBestList.add(TodayBest.init(userInfo, item));
                }
            }
            result.setItems(todayBestList);
        }
        return result;
    }
}
