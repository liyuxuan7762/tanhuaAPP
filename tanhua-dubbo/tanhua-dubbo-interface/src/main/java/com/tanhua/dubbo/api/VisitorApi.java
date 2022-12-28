package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;

import java.util.List;

public interface VisitorApi {
    void save(Visitors visitors);

    List<Visitors> getVisitors(Long userId, Long time);
}
