package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Visitors;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class VisitorApiImpl implements VisitorApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void save(Visitors visitors) {
        // 首先需要判断一下当前是否已经保存过该访问记录
        // 判断条件是 用户id 访客id 日期
        Query query = new Query(
                Criteria.where("userId").is(visitors.getUserId())
                        .and("visitorUserId").is(visitors.getVisitorUserId())
                        .and("visitDate").is(visitors.getVisitDate())
        );
        if (!this.mongoTemplate.exists(query, Visitors.class)) {
            this.mongoTemplate.save(visitors);
        }
    }

    /**
     * 查询访客
     *
     * @param userId
     * @param time
     * @return
     */
    @Override
    public List<Visitors> getVisitors(Long userId, Long time) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (time != null) {
            criteria.and("date").gt(time);
        }
        Query query = new Query(criteria).limit(5).with(Sort.by(Sort.Order.desc("date")));
        return this.mongoTemplate.find(query, Visitors.class);
    }
}
