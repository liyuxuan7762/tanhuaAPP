package com.tanhua.dubbo.api;

import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.PageResult;

import java.util.List;

public interface CommentApi {
    Integer save(Comment newComment, int type);

    List<Comment> getCommentListByMovementId(Integer page, Integer pagesize, String movementId);

    Boolean check(String movementApi, int type, Long userId);

    Integer delete(Comment comment, int type);

    List<Comment> getCommentByType(Long userId, Integer page, Integer pagesize, int type);

    PageResult getCommentsByMovementId(Integer page, Integer pagesize, Long messageID);
}
