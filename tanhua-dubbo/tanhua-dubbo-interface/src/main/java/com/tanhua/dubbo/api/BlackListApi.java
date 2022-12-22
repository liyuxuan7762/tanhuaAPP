package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.model.domain.UserInfo;

public interface BlackListApi {
    IPage<UserInfo> getBlackList(Long userId, int page, int size);
    void removeUserFromBlackList(Long userId, Long uid);
}
