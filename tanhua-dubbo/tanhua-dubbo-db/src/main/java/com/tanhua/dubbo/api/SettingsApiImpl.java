package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class SettingsApiImpl implements SettingsApi{
    @Resource
    private SettingsMapper settingsMapper;
    @Override
    public Settings getSettingsByUserId(Long userId) {
        LambdaQueryWrapper<Settings> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Settings::getUserId, userId);
        return this.settingsMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(Settings settings) {
        this.settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        this.settingsMapper.updateById(settings);
    }
}
