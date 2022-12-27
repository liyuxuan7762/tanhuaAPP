package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserApi;
import com.tanhua.model.domain.User;
import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class HuanXinService {
    @DubboReference
    private UserApi userApi;
    public HuanXinUserVo getHuanXinUser() {
        Long userId = UserHolder.getUserId();
        // 根据登录用户查询环信用户
        User user = this.userApi.getHuanXinUser(userId);
        return new HuanXinUserVo(user.getHxUser(), user.getHxPassword());
    }
}
