package com.tanhua.dubbo.api;

import com.tanhua.model.domain.UserInfo;

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
}
