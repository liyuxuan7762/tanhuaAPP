package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.RecommendUser;
import com.tanhua.model.mongo.UserLike;
import com.tanhua.model.vo.PageResult;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class RecommendUserApiImpl implements RecommendUserApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public RecommendUser getTodayBest(Long userId) {
        // 根据toUserId字段查询，然后根据分数排序，取分数最高的一个
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = new Query().with(Sort.by(Sort.Order.desc("score"))).limit(1);
        return mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public PageResult getRecommendationList(Integer page, Integer pagesize, Long userId) {
        // 1. 构建查询条件 查询总数
        Criteria criteria = Criteria.where("toUserId").is(userId);
        Query query = new Query(criteria);
        long count = this.mongoTemplate.count(query, RecommendUser.class);
        // 2. 构造分页条件
        query.skip((page - 1) * pagesize).limit(pagesize).with(Sort.by(Sort.Order.desc("score")));
        List<RecommendUser> recommendUsers = this.mongoTemplate.find(query, RecommendUser.class);
        // 3，构造结果
        return new PageResult(page, pagesize, (int) count, recommendUsers);
    }

    @Override
    public RecommendUser getRecommendUserByUserId(Long userId) {
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        return this.mongoTemplate.findOne(query, RecommendUser.class);
    }

    @Override
    public List<RecommendUser> getCards(Long userId, int count) {
        // 1. 先到like表中查询当前用户已经喜欢或不喜欢的用户id
        Query query = new Query(Criteria.where("userId").is(userId));
        List<UserLike> userLikes = this.mongoTemplate.find(query, UserLike.class);
        List<Long> likedIds = CollUtil.getFieldValues(userLikes, "likeUserId", Long.class);

        // 2. 构造条件
        Criteria criteria = Criteria.where("toUserId").is(userId).and("userId").nin(likedIds);

        TypedAggregation<RecommendUser> aggregation = TypedAggregation.newAggregation(
          RecommendUser.class,
          Aggregation.match(criteria),
                Aggregation.sample(count)
        );

        return this.mongoTemplate.aggregate(aggregation, RecommendUser.class).getMappedResults();
    }
}
