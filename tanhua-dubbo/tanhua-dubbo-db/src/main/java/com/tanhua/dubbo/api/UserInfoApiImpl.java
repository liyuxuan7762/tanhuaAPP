package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        this.userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        this.userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo getUserInfoById(Long userID) {
        return this.userInfoMapper.selectById(userID);
    }


}
