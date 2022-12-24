package com.tanhua.model.mongo;

import com.tanhua.model.enums.CommentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

//动态详情表
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movement")
public class Movement implements java.io.Serializable {

    private ObjectId id; //主键id
    private Long pid; //Long类型，用于推荐系统的模型(自动增长) 利用MongoDB实现自增
    private Long created; //发布时间
    private Long userId;
    private String textContent; //文字
    private List<String> medias; //媒体数据，图片或小视频 url
    private String longitude; //经度
    private String latitude; //纬度
    private String locationName; //位置名称
    private Integer state = 0;//状态 0：未审（默认），1：通过，2：驳回

    //补充字段
    private Integer likeCount = 0; //点赞数
    private Integer commentCount = 0; //评论数
    private Integer loveCount = 0; //喜欢数

    //根据评论类型，获取对应的互动数量
    public Integer getCount(Integer commentType) {
        if (commentType == CommentType.LIKE.getType()) {
            return this.likeCount;
        } else if (commentType == CommentType.COMMENT.getType()) {
            return this.commentCount;
        } else {
            return loveCount;
        }
    }
}