package com.tanhua.admin.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.tanhua.common.utils.Constants.USER_FREEZE;

@Service
public class ManageService {
    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private VideoApi videoApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private CommentApi commentApi;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public PageResult getUsers(Integer page, Integer pagesize) {
        IPage<UserInfo> iPage = this.userInfoApi.getUsers(page, pagesize);
        List<UserInfo> userInfoList = iPage.getRecords();
        for (UserInfo userInfo : userInfoList) {
            String key = USER_FREEZE + userInfo.getId();
            if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
                userInfo.setUserStatus("2");
            }
        }
        return new PageResult(page, pagesize, (int) iPage.getTotal(), iPage.getRecords());
    }

    public UserInfo getUserByUserId(Long userId) {
        UserInfo userInfo = this.userInfoApi.getUserInfoById(userId);
        String key = USER_FREEZE + userInfo.getId();
        if (Boolean.TRUE.equals(this.redisTemplate.hasKey(key))) {
            userInfo.setUserStatus("2");
        }
        return userInfo;
    }

    public PageResult getVideosByUserId(Integer page, Integer pagesize, Long uid) {
        return this.videoApi.getVideosByUserId(page, pagesize, uid);
    }

    public PageResult getMovementByUserId(Integer page, Integer pagesize, Long uid, Integer state) {
        PageResult result = this.movementApi.getMovementByUserId(uid, state, page, pagesize);
        List<Movement> items = (List<Movement>) result.getItems();
        if (items.isEmpty()) {
            return new PageResult();
        }

        List<Long> ids = CollUtil.getFieldValues(items, "userId", Long.class);
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);

        List<MovementsVo> voList = new ArrayList<>();
        for (Movement item : items) {
            Long id = item.getUserId();
            UserInfo userInfo = map.get(id);
            if (userInfo != null) {
                voList.add(MovementsVo.init(userInfo, item));
            }
        }

        result.setItems(voList);
        return result;
    }

    public MovementsVo getMovementDetailById(Long movementId) {
        Movement movement = this.movementApi.getMovementById(movementId.toString());
        Long id = movement.getUserId();
        UserInfo userInfo = this.userInfoApi.getUserInfoById(id);
        return MovementsVo.init(userInfo, movement);
    }


    public PageResult getCommentsByMovementId(Integer page, Integer pagesize, Long messageID) {
        return this.commentApi.getCommentsByMovementId(page, pagesize, messageID);
    }

    public Map freeze(Map map) {
        // 1. 获取用户id，拼接key
        String userId = map.get("userId").toString();
        String key = USER_FREEZE + userId;
        // 2. 读取冻结时间 1为冻结3天，2为冻结7天，3为永久冻结
        Integer freezingTime = Integer.valueOf(map.get("freezingTime").toString());
        int day = 0;
        switch (freezingTime) {
            case 1:
                day = 3;
                break;
            case 2:
                day = 7;
                break;
        }
        // 3. 保存到Redis
        String str = JSON.toJSONString(map);
        if (day > 0) {
            this.redisTemplate.opsForValue().set(key, str, day, TimeUnit.DAYS);
        } else {
            this.redisTemplate.opsForValue().set(key, str);
        }

        Map retMap = new HashMap();
        retMap.put("message", "冻结成功");
        return retMap;
    }

    public Map unfreeze(Map map) {
        String userId = map.get("userId").toString();
        String key = USER_FREEZE + userId;
        this.redisTemplate.delete(key);
        Map retMap = new HashMap();
        retMap.put("message", "解冻成功");
        return retMap;
    }
}
