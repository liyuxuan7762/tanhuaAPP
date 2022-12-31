package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("recommend_video_score")
public class VideoScore {

    private ObjectId id;
    private Long userId;// 用户id
    private Long videoId; //视频id，需要转化为Long类型 video中的vid字段
    private Double score; //得分
    private Long date; //时间戳
}
