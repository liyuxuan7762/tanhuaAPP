package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "recommend_user")
public class RecommendUser implements Serializable {
    @Id
    private ObjectId id; //主键id
    private Long userId; //推荐的用户id
    private Long toUserId; //用户id
    private Double score =0d; //推荐得分
    private String date; //日期
}