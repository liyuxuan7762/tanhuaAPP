package com.tanhua.recommend.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.common.utils.Constants;
import com.tanhua.model.mongo.MovementScore;
import com.tanhua.model.mongo.Video;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class RecommendVideoListener {

    @Resource
    private MongoTemplate mongoTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "tanhua.video.queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            Constants.LOG_EXCHANGE_NAME
                    ),
                    key = "log.video"
            )
    )
    public void recommend(String msg) {
        try {
            // 解析数据
            Map<String, String> map = JSON.parseObject(msg, Map.class);
            String userId = map.get("userId");
            String type = map.get("type");
            String logTime = map.get("logTime");
            String videoId = map.get("busId");

            // 根据动态id查询到动态对象
            Video video = this.mongoTemplate.findById(videoId, Video.class);

            if (video != null) {
                // 封装
                MovementScore score = new MovementScore();
                score.setUserId(Long.parseLong(userId));
                score.setDate(System.currentTimeMillis());
                score.setMovementId(video.getVid());
                score.setScore(getScore(type));
                this.mongoTemplate.save(score);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }

    private static Double getScore(String type) {
        //0301为发小视频，0302为小视频点赞，0303为小视频取消点赞，0304为小视频评论
        Double score = 0d;
        switch (type) {
            case "0301":
                score = 2d;
                break;
            case "0302":
                score = 5d;
                break;
            case "0303":
                score = -5d;
                break;
            case "0304":
                score = 10d;
                break;
            default:
                break;
        }
        return score;
    }
}
