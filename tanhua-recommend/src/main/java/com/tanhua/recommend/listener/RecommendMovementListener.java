package com.tanhua.recommend.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.tanhua.common.utils.Constants;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.MovementScore;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class RecommendMovementListener {

    @Resource
    private MongoTemplate mongoTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "tanhua.movement.queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            Constants.LOG_EXCHANGE_NAME
                    ),
                    key = "log.movement"
            )
    )
    public void recommend(String msg) {
        try {
            // 解析数据
            Map<String, String> map = JSON.parseObject(msg, Map.class);
            String userId = map.get("userId");
            String type = map.get("type");
            String logTime = map.get("logTime");
            String movementId = map.get("busId");

            // 根据动态id查询到动态对象
            Movement movement = this.mongoTemplate.findById(movementId, Movement.class);

            if (movement != null) {
                // 封装
                MovementScore score = new MovementScore();
                score.setUserId(Long.parseLong(userId));
                score.setDate(System.currentTimeMillis());
                score.setMovementId(movement.getPid());
                score.setScore(getScore(type, movement));
                this.mongoTemplate.save(score);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

    }

    private static Double getScore(String type, Movement movement) {
        //0201为发动态  基础5分 50以内1分，50~100之间2分，100以上3分
        //0202为浏览动态， 1
        //0203为动态点赞， 5
        //0204为动态喜欢， 8
        //0205为评论，     10
        //0206为动态取消点赞， -5
        //0207为动态取消喜欢   -8
        Double score = 0d;
        switch (type) {
            case "0201":
                score = 5d;
                score += movement.getMedias().size();
                int length = StrUtil.length(movement.getTextContent());
                if (length >= 0 && length < 50) {
                    score += 1;
                } else if (length < 100) {
                    score += 2;
                } else {
                    score += 3;
                }
                break;
            case "0202":
                score = 1d;
                break;
            case "0203":
                score = 5d;
                break;
            case "0204":
                score = 8d;
                break;
            case "0205":
                score = 10d;
                break;
            case "0206":
                score = -5d;
                break;
            case "0207":
                score = -8d;
                break;
            default:
                break;
        }
        return score;
    }
}
