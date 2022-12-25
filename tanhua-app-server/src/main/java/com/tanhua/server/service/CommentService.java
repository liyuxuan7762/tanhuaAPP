package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.bson.types.ObjectId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tanhua.common.utils.Constants.*;

@Service
public class CommentService {
    @DubboReference
    private CommentApi commentApi;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
                CommentVo init = CommentVo.init(userInfo, comment);
                String key = MOVEMENTS_INTERACT_KEY + comment.getId();
                // 构造哈希key prefix + userId
                String likeHashKey = MOVEMENT_LIKE_HASHKEY + userId;
                if (this.redisTemplate.opsForHash().hasKey(key, likeHashKey)) {
                    init.setHasLiked(1);
                }
                commentVos.add(init);
            }
        }
        // 5. 返回结果
        return new PageResult(page, pagesize, 0, commentVos);
    }

    /**
     * 用户点赞
     *
     * @param movementId 动态ID
     * @return 点赞之后最新的点赞数量
     */
    public Integer like(String movementId) {
//        // 1. 获取当前用户ID
//        Long userId = UserHolder.getUserId();
//        // 2. 查询comment表，判断用户是否已经点过赞，如果点过，则抛出异常
//        Boolean isLiked = this.commentApi.check(movementId, CommentType.LIKE.getType(), userId);
//        if (isLiked) {
//            // 用户已经点过赞了，这里就直接抛出异常
//            throw new BusinessException(ErrorResult.likeError());
//        }
//        // 3. 封装Comment对象，调用api保存comment
//        Comment comment = new Comment();
//        comment.setPublishId(new ObjectId(movementId));
//        comment.setUserId(userId);
//        comment.setCreated(System.currentTimeMillis());
//        Integer count = this.commentApi.save(comment, CommentType.LIKE.getType());
//        // 4. 将用户点赞保存到Redis
//        // 构造key prefix + movementId
//        String key = MOVEMENTS_INTERACT_KEY + movementId;
//        // 构造哈希key prefix + userId
//        String hashKey = MOVEMENT_LIKE_HASHKEY + userId;
//        this.redisTemplate.opsForHash().put(key, hashKey, "1");
//        // 5. 返回结果
        return this.processLikeOrLove(movementId, CommentType.LIKE.getType(), true);
    }

    /**
     * 取消点赞
     *
     * @param movementId 动态Id
     * @return 取消点赞之后最新的点赞数量
     */
    public Integer dislike(String movementId) {
//        // 1. 获取当前用户id
//        Long userId = UserHolder.getUserId();
//        // 2. 查询用户是否点过赞，如果没有点过赞，则抛出异常
//        Boolean isLiked = this.commentApi.check(movementId, CommentType.LIKE.getType(), userId);
//        if (!isLiked) {
//            // 用户已经点过赞了，这里就直接抛出异常
//            throw new BusinessException(ErrorResult.disLikeError());
//        }
//        // 3. 调用api取消点赞
//        Comment comment = new Comment();
//        comment.setPublishId(new ObjectId(movementId));
//        comment.setUserId(userId);
//        Integer count = this.commentApi.delete(comment, CommentType.LIKE.getType());
//        // 4. 删除redis中的键
//        // 构造key prefix + movementId
//        String key = MOVEMENTS_INTERACT_KEY + movementId;
//        // 构造哈希key prefix + userId
//        String hashKey = MOVEMENT_LIKE_HASHKEY + userId;
//        this.redisTemplate.opsForHash().delete(key, hashKey);
//        // 5. 返回结果
//        return count;
        return this.processLikeOrLove(movementId, CommentType.LIKE.getType(), false);
    }

    /**
     * 喜欢动态
     *
     * @param movementId 动态Id
     * @return
     */
    public Integer love(String movementId) {
        return this.processLikeOrLove(movementId, CommentType.LOVE.getType(), true);
    }

    /**
     * 用户取消喜欢动态
     * @param movementId 动态Id
     * @return
     */
    public Integer unlove(String movementId) {
        return this.processLikeOrLove(movementId, CommentType.LOVE.getType(), false);
    }

    /**
     * 点赞评论
     * @param commentId 评论id
     * @return
     */
    public Integer likeComment(String commentId) {
        return this.processLikeOrLove(commentId, CommentType.LIKECOMMENT.getType(), true);
    }

    /**
     * 处理喜欢和点赞请求
     *
     * @param movementId 动态id
     * @param type       类型 判断是喜欢还是点赞
     * @param flag       标志位 true表示新增 false表示删除
     * @return
     */
    private Integer processLikeOrLove(String movementId, Integer type, Boolean flag) {
        // 1. 根据用户id，判断用户是否已经喜欢过或者点过赞
        Long userId = UserHolder.getUserId();
        Boolean check = this.commentApi.check(movementId, type, userId);
        if (!flag) {
            check = !check;
        }
        if (check) {
            switch (type) {
                case 1:
                case 4:
                    throw new BusinessException(ErrorResult.likeError());
                case 3:
                    throw new BusinessException(ErrorResult.loveError());
            }
        }
        // 2. 用户没有点过赞或者喜欢过  封装Comment对象，调用api保存comment
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setUserId(userId);
        comment.setCreated(System.currentTimeMillis());

        // 3. 设置Redis
        String key = MOVEMENTS_INTERACT_KEY + movementId;
        // 构造哈希key prefix + userId
        String likeHashKey = MOVEMENT_LIKE_HASHKEY + userId;
        String loveHashKey = MOVEMENT_LOVE_HASHKEY + userId;

        Integer count;
        if (flag) {
            // 插入数据
            count = this.commentApi.save(comment, type);
            if (type == CommentType.LOVE.getType()) {
                this.redisTemplate.opsForHash().put(key, loveHashKey, "1");
            } else if (type == CommentType.LIKE.getType() || type == CommentType.LIKECOMMENT.getType()) {
                this.redisTemplate.opsForHash().put(key, likeHashKey, "1");
            }
        } else {
            // 删除数据
            count = this.commentApi.delete(comment, type);
            if (type == CommentType.LOVE.getType()) {
                this.redisTemplate.opsForHash().delete(key, loveHashKey);
            } else if (type == CommentType.LIKE.getType() || type == CommentType.LIKECOMMENT.getType()) {
                this.redisTemplate.opsForHash().delete(key, likeHashKey);
            }
        }
        return count;
    }


    public Integer dislikeComment(String commentId) {
        return this.processLikeOrLove(commentId, CommentType.LIKECOMMENT.getType(), false);
    }
}
