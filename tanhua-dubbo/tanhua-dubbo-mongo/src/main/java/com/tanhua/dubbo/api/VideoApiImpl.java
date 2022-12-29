package com.tanhua.dubbo.api;

import com.tanhua.dubbo.utils.IdWorker;
import com.tanhua.model.mongo.Video;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class VideoApiImpl implements VideoApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private IdWorker idWorker;

    @Override
    public String save(Video video) {
        video.setVid(idWorker.getNextId("video"));
        video.setCreated(System.currentTimeMillis());

        return this.mongoTemplate.save(video).getId().toHexString();
    }

    @Override
    public List<Video> getVideoByPids(List<Long> vids) {
        Query query = new Query(Criteria.where("vid").in(vids));
        return this.mongoTemplate.find(query, Video.class);
    }

    @Override
    public List<Video> getVideos(int page, Integer pagesize) {
        Query query = new Query();
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("created")));
        return this.mongoTemplate.find(query, Video.class);
    }
}
