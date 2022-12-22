package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.dubbo.api.QuestionApi;
import com.tanhua.dubbo.api.SettingsApi;
import com.tanhua.model.domain.Question;
import com.tanhua.model.domain.Settings;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingsService {

    @DubboReference
    private QuestionApi questionApi;

    @DubboReference
    private SettingsApi settingsApi;

    @DubboReference
    private BlackListApi blackListApi;

    public SettingsVo getSettings() {
        SettingsVo settingsVo = new SettingsVo();
        // 1. 获取到用户的id
        settingsVo.setId(UserHolder.getUserId());
        // 2. 获取到用户的手机号
        settingsVo.setPhone(UserHolder.getUserPhone());
        // 3. 根据用户id查询陌生人问题
        Question question = this.questionApi.getQuestionByUserId(UserHolder.getUserId());
        String txt = question == null ? "用户没有设置陌生人问题" : question.getTxt();
        settingsVo.setStrangerQuestion(txt);
        // 4. 查询设置开关状态
        Settings settings = this.settingsApi.getSettingsByUserId(UserHolder.getUserId());
        if (settings != null) {
            settingsVo.setLikeNotification(settings.getLikeNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
        }
        return settingsVo;
    }

    public void saveOrUpdateQuestion(String content) {
        // 1. 获取用户ID
        Long userId = UserHolder.getUserId();
        // 2. 判断用户是否设置过问题 如果设置过 则为更新 否则为新增
        Question question = this.questionApi.getQuestionByUserId(userId);
        if (question == null) {
            // 新增问题
            question.setUserId(userId);
            question.setTxt(content);
            this.questionApi.save(question);
        } else {
            // 更新问题
            question.setTxt(content);
            this.questionApi.update(question);
        }
    }

    public void saveOrUpdateSettings(Map map) {
        // 0. 解析数据
        Boolean likeNotification = (Boolean) map.get("likeNotification");
        Boolean pinglunNotification = (Boolean) map.get("pinglunNotification");
        Boolean gonggaoNotification = (Boolean) map.get("gonggaoNotification");
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 查询用户是否有过设置 如果没有 则新增 否则则更新
        Settings settings = this.settingsApi.getSettingsByUserId(userId);
        if (settings == null) {
            settings = new Settings();
            settings.setUserId(userId);
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            this.settingsApi.save(settings);
        } else {
            settings.setLikeNotification(likeNotification);
            settings.setPinglunNotification(pinglunNotification);
            settings.setGonggaoNotification(gonggaoNotification);
            this.settingsApi.update(settings);
        }
    }

    public PageResult getBlackList(int page, int size) {
        // 1. 获取到用户id
        Long userId = UserHolder.getUserId();
        // 2. 调用api查询
        IPage<UserInfo> iPage = this.blackListApi.getBlackList(userId, page, size);
        // 3. 解析page对象成PageResult
        return new PageResult(page, size, (int) iPage.getTotal(), iPage.getRecords());

    }

    public void removeUserFromBlackList(Long uid) {
        // 1. 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 2. 调用api删除
        this.blackListApi.removeUserFromBlackList(userId, uid);
    }
}
