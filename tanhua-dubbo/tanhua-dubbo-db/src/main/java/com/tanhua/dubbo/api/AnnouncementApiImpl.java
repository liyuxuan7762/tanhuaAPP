package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tanhua.dubbo.mappers.AnnouncementMapper;
import com.tanhua.model.domain.Announcement;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class AnnouncementApiImpl implements AnnouncementApi {

    @Resource
    private AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> getAnnouncements(Integer page, Integer pagesize) {
        Page<Announcement> announcementPage = new Page<>(page, pagesize);
        QueryWrapper<Announcement> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created");
        return announcementMapper.selectPage(announcementPage, queryWrapper).getRecords();
    }
}
