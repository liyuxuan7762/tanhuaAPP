package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface VideoApi {
    String save(Video video);

    List<Video> getVideoByPids(List<Long> vids);

    List<Video> getVideos(int page, Integer pagesize);

    PageResult getVideosByUserId(Integer page, Integer pagesize, Long uid);
}
