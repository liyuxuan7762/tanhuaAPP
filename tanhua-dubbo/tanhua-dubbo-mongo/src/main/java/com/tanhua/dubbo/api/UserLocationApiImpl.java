package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.model.mongo.UserLocation;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@DubboService
public class UserLocationApiImpl implements UserLocationApi {
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public boolean saveOrUpdate(Long userId, double latitude, double longitude, String addrStr) {
        // 构建条件
        Query query = new Query(Criteria.where("userId").is(userId));
        // 如果存在则更新，否则新增
        UserLocation location = this.mongoTemplate.findOne(query, UserLocation.class);
        try {
            if (location == null) {
                location = new UserLocation();
                location.setUserId(userId);
                location.setLocation(new GeoJsonPoint(longitude, latitude));
                Long time = System.currentTimeMillis();
                location.setUpdated(time);
                location.setCreated(time);
                location.setLastUpdated(time);
                location.setAddress(addrStr);
                this.mongoTemplate.save(location);
            } else {
                Update update = new Update();
                update.set("location", new GeoJsonPoint(longitude, latitude));
                update.set("updated", System.currentTimeMillis());
                update.set("LastUpdated", location.getUpdated());
                this.mongoTemplate.updateFirst(query, update, UserLocation.class);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Long> getNearPeople(Long userId, String gender, String distance) {
        // 1. 根据用户id，查询到登录用户的位置
        Query query = new Query(Criteria.where("userId").is(userId));
        UserLocation userLocation = this.mongoTemplate.findOne(query, UserLocation.class);
        if (userLocation.getLocation() == null) {
            return new ArrayList<>();
        }
        // 2. 查询附近的人
        GeoJsonPoint point = userLocation.getLocation(); // 圆心
        Distance dis = new Distance(distance / 1000, Metrics.KILOMETERS); // 半径
        Circle circle = new Circle(point, dis);
        Query nearQuery = new Query(Criteria.where("location").withinSphere(circle));
        List<UserLocation> userLocations = this.mongoTemplate.find(query, UserLocation.class);

        // 3. 提取出ids返回
        return CollUtil.getFieldValues(userLocations, "userId", Long.class);
    }
}
