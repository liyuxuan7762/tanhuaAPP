package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.BlackListMapper;
import com.tanhua.model.domain.BlackList;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class BlackListApiImpl implements BlackListApi{
    @Resource
    private BlackListMapper blackListMapper;

    @Override
    public IPage<UserInfo> getBlackList(Long userId, int page, int size) {
        Page pageInfo = new Page(page, size);
        return this.blackListMapper.getBlackList(userId, pageInfo);
    }

    @Override
    public void removeUserFromBlackList(Long userId, Long uid) {
        LambdaQueryWrapper<BlackList> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlackList::getBlackUserId, uid);
        queryWrapper.eq(BlackList::getUserId, userId);
        this.blackListMapper.delete(queryWrapper);
    }
}
