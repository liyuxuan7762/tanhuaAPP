package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

//大数据动态评分实体类
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("recommend_movement_score")
public class MovementScore {

    private ObjectId id;
    private Long userId;// 用户id
    private Long movementId; //动态id，需要转化为Long类型
    private Double score; //得分
    private Long date; //时间戳
}