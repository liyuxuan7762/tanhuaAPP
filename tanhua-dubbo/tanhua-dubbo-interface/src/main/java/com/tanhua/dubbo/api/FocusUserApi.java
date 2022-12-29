package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.FocusUser;

public interface FocusUserApi {
    void save(FocusUser focusUser);

    void delete(FocusUser focusUser);
}
