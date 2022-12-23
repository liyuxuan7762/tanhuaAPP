package com.tanhua.dubbo.api;

import com.tanhua.model.domain.UserInfo;

import java.util.List;
import java.util.Map;

public interface UserInfoApi {

    /**
     * 保存用户详细信息
     * @param userInfo
     */
    void save(UserInfo userInfo);

    /**
     * 更新用户详细信息
     * @param userInfo
     */
    void update(UserInfo userInfo);

    UserInfo getUserInfoById(Long userID);

    Map<Long, UserInfo> getUserInfoByIds(List<Long> ids, UserInfo condition);

}
