package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.*;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.vo.*;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.tanhua.common.utils.Constants.HX_USER_PREFIX;

@Service
public class TanhuaService {

    @DubboReference
    private RecommendUserApi recommendUserApi;
    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private UserLikeApi userLikeApi;

    @Resource
    private HuanXinTemplate huanXinTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private MessageService messageService;

    @DubboReference
    private UserLocationApi userLocationApi;


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

    /**
     * 查询佳人详情
     *
     * @param userId 用户id
     * @return
     */
    public TodayBest getTodayBestById(Long userId) {
        // 1. 查询UserInfo表
        UserInfo userInfo = this.userInfoApi.getUserInfoById(userId);
        // 2. 查询RecommendUser表
        RecommendUser user = this.recommendUserApi.getRecommendUserByUserId(userId);
        // 3. 构建VO
        return TodayBest.init(userInfo, user);
    }

    public String getQuestionByUserId(Long userId) {
        return this.questionApi.getQuestionByUserId(userId);
    }

    /**
     * 回复陌生人问题
     *
     * @param userId 收件人用户id
     * @param reply  回复内容
     */
    public void replyQuestion(Long userId, String reply) {
        // 1. 发送信息包含 当前用户id 当前用户环信id，昵称，问题和回答
        Long currentUserId = UserHolder.getUserId();
        String currentHxId = HX_USER_PREFIX + currentUserId;
        String nickName = this.userInfoApi.getUserInfoById(currentUserId).getNickname();
        String question = this.questionApi.getQuestionByUserId(userId);

        Map map = new HashMap();
        map.put("userId", currentUserId);
        map.put("huanXinId", currentHxId);
        map.put("nickname", nickName);
        map.put("strangerQuestion", question);
        map.put("reply", reply);
        String msg = JSON.toJSONString(map);
        // 2. 调用template发送消息
        Boolean flag = this.huanXinTemplate.sendMsg(HX_USER_PREFIX + userId, msg);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }


    /**
     * 获取左滑右滑数据
     *
     * @return
     */
    public List<TodayBest> getCards() {
        Long userId = UserHolder.getUserId();
        // 1. 调用API 查询到所有符合要求的RecommendUser集合
        List<RecommendUser> recommendUserList = this.recommendUserApi.getCards(userId, 10);
        // 2. 根据集合，查询出所有的用户详情
        List<Long> ids = CollUtil.getFieldValues(recommendUserList, "userId", Long.class);
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        // 3. 封装VO
        List<TodayBest> list = new ArrayList<>();
        for (RecommendUser recommendUser : recommendUserList) {
            Long id = recommendUser.getUserId();
            UserInfo userInfo = map.get(id);
            if (userInfo != null) {
                list.add(TodayBest.init(userInfo, recommendUser));
            }
        }

        return list;
    }

    /**
     * 左滑喜欢
     *
     * @param likeUserId
     */
    public void loveUser(Long likeUserId) {
        // 1. 将用户喜欢的信息保存到UserLike表
        boolean flag = this.userLikeApi.save(UserHolder.getUserId(), likeUserId, true);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 将用户喜欢信息保存到Redis
        this.redisTemplate.opsForSet().add(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        this.redisTemplate.opsForSet().remove(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        // 3. 判断是否为双向喜欢
        boolean like = isLike(likeUserId, UserHolder.getUserId());
        if (like) {
            // 双向喜欢 添加好友
            this.messageService.contacts(likeUserId);
        }
    }

    /**
     * 判断userId是否喜欢likeUserId
     *
     * @param userId
     * @param likeUserId
     * @return
     */
    private boolean isLike(Long userId, Long likeUserId) {
        return this.redisTemplate.opsForSet().isMember(Constants.USER_LIKE_KEY + userId, likeUserId);
    }

    public void unloveUser(Long likeUserId) {
        // 1. 将用户喜欢的信息保存到UserLike表
        boolean flag = this.userLikeApi.save(UserHolder.getUserId(), likeUserId, false);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 将用户喜欢信息保存到Redis
        this.redisTemplate.opsForSet().remove(Constants.USER_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
        this.redisTemplate.opsForSet().add(Constants.USER_NOT_LIKE_KEY + UserHolder.getUserId(), likeUserId.toString());
    }

    public List<NearUserVo> getNearPeople(String gender, String distance) {
        // 1. 获取到附近人的用户id
        Long userId = UserHolder.getUserId();
        List<Long> ids = this.userLocationApi.getNearPeople(userId, gender, distance);
        // 2. 根据用户id查询用户详情
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        // 3. 封装VO 注意排除当前用户
        List<NearUserVo> voList = new ArrayList<>();
        for (Long id : ids) {
            if (Objects.equals(id, userId)) {
                // 用户自己
                continue;
            }
            UserInfo userInfo = map.get(id);
            if (userInfo != null) {
                voList.add(NearUserVo.init(userInfo));
            }
        }
        return voList;
    }
}
