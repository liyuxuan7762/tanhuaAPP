package com.tanhua.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 好友时间线表，用于存储好友发布的数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "movement_timeLine")
public class MovementTimeLine implements java.io.Serializable {

    private static final long serialVersionUID = 9096178416317502524L;
    private ObjectId id;
    private ObjectId movementId;//动态id
    private Long userId;   //发布动态用户id
    private Long friendId; // 可见好友id
    private Long created; //发布的时间
}
