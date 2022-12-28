package com.tanhua.dubbo.api;

import java.util.List;

public interface UserLocationApi {
    boolean saveOrUpdate(Long userId, double latitude, double longitude, String addrStr);

    List<Long> getNearPeople(Long userId, String gender, String distance);
}
