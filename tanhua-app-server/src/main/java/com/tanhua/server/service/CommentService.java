package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    /**
     * 根据动态id和评论正文 新增一条评论
     *
     * @param movementId 动态id
     * @param comment    评论正文
     */
    public Integer publishComment(String movementId, String comment) {
        // 1. 根据动态ID查询到动态的对象
        Comment newComment = new Comment();
        // 4. 继续封装其他的Comment属性
        newComment.setPublishId(new ObjectId(movementId));
        newComment.setContent(comment);
        newComment.setUserId(UserHolder.getUserId());
        newComment.setCreated(System.currentTimeMillis());
        // 5. 调用方法保存Comment, 并且返回保存后的评论数
        return this.commentApi.save(newComment, CommentType.COMMENT.getType());
    }

    /**
     * 根据动态ID查询到动态的所有的评论列表
     *
     * @param page       页号
     * @param pagesize   页大小
     * @param movementId 动态ID
     * @return
     */
    public PageResult getCommentListByMovementId(Integer page, Integer pagesize, String movementId) {
        // 1. 根据动态ID查询到所有的评论集合
        List<Comment> commentList = this.commentApi.getCommentListByMovementId(page, pagesize, movementId);
        if (CollUtil.isEmpty(commentList)) {
            return new PageResult();
        }
        // 2. 从评论集合中抽取出userId评论发布人的id
        List<Long> userIds = CollUtil.getFieldValues(commentList, "userId", Long.class);
        // 3. 根据发布人的Id去查询对应的用户详情
        Map<Long, UserInfo> userInfoByIds = this.userInfoApi.getUserInfoByIds(userIds, null);
        // 4. 封装Vo对象
        List<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : commentList) {
            Long userId = comment.getUserId();
            UserInfo userInfo = userInfoByIds.get(userId);
            if (userInfo != null) {
                commentVos.add(CommentVo.init(userInfo, comment));
            }
        }
        // 5. 返回结果
        return new PageResult(page, pagesize, 0, commentVos);
    }
}
