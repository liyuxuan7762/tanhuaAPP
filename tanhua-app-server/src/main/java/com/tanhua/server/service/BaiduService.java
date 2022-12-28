package com.tanhua.server.service;

import com.tanhua.dubbo.api.UserLocationApi;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class BaiduService {
    @DubboReference
    private UserLocationApi userLocationApi;

    public void uploadLocation(double latitude, double longitude, String addrStr) {
        Long userId = UserHolder.getUserId();
        boolean flag = this.userLocationApi.saveOrUpdate(userId, latitude, longitude, addrStr);
        if (!flag) {
            throw new BusinessException(ErrorResult.error());
        }
    }
}
