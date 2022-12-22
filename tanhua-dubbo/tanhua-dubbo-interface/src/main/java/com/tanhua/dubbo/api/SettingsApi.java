package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Settings;

public interface SettingsApi {
    Settings getSettingsByUserId(Long userId);
    void save(Settings settings);
    void update(Settings settings);
}
