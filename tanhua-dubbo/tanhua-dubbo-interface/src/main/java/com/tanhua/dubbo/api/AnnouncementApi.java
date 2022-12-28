package com.tanhua.dubbo.api;

import com.tanhua.model.domain.Announcement;

import java.util.List;

public interface AnnouncementApi {
    List<Announcement> getAnnouncements(Integer page, Integer pagesize);
}
