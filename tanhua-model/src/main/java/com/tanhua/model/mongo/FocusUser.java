package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

//用户关注表（关注小视频的发布作者）
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "focus_user")
public class FocusUser implements java.io.Serializable{

    private static final long serialVersionUID = 3148619072405056052L;

    private ObjectId id; //主键id
    private Long userId; //用户id    106
    private Long followUserId; //关注的用户id   1
    private Long created; //关注时间
}