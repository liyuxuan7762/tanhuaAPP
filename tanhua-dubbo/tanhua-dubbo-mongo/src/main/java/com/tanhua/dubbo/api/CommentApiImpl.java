package com.tanhua.dubbo.api;

import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class CommentApiImpl implements CommentApi {

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存Comment到数据库
     *
     * @param comment 评论对象
     * @param type    评论类型
     * @return
     */
    @Override
    public Integer save(Comment comment, int type) {
        // 1. 从Comment对象中获取到动态id，查询动态id，
        try {
            ObjectId publishId = comment.getPublishId();
            Movement movementById = this.mongoTemplate.findById(publishId, Movement.class);
            if (movementById != null || type == CommentType.LIKECOMMENT.getType()) {
                if (movementById == null) {
                    // 1. 获取到动态id的作者 并设置到Comment的publishUserId字段中
                    Comment publishUser = this.mongoTemplate.findById(comment.getPublishId(), Comment.class);
                    if (publishUser != null) {
                        comment.setPublishUserId(publishUser.getPublishUserId());
                    } else {
                        return 0;
                    }
                } else {
                    comment.setPublishUserId(movementById.getUserId());
                }
                // 2. 设置评论的类型
                comment.setCommentType(type);
                // 3. 保存到数据库
                this.mongoTemplate.save(comment);
                // 4. 根据不同的评论类型，更新Movement或者Comment表中数据表中对象的记录
                // 4.1 构造查询条件 如果是动态的点赞或者喜欢
                if (type == CommentType.LIKECOMMENT.getType()) {
                    // 评论点赞
                    Query query = new Query(Criteria.where("id").is(comment.getPublishId()));
                    Update update = new Update();
                    update.inc("likeCount", 1);
                    FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
                    return this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, Comment.class).getLikeCount();
                } else {
                    Criteria criteria = Criteria.where("id").is(movementById.getId());
                    Query query = new Query(criteria);
                    Update update = new Update();
                    Integer commentType = comment.getCommentType();
                    if (commentType == CommentType.LIKE.getType()) {
                        update.inc("likeCount", 1);
                    } else if (commentType == CommentType.COMMENT.getType()) {
                        update.inc("commentCount", 1);
                    } else {
                        update.inc("loveCount", 1);
                    }
                    FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
                    // 调用template更新 返回更新后的Movement对象
                    Movement modify = this.mongoTemplate.findAndModify(query, update, findAndModifyOptions, Movement.class);

                    // 根据不同的评论类型，返回对应的计数
                    return modify.getCount(commentType);
                }
            } else {
                return 0;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询指定动态ID的所有的评论
     *
     * @param page
     * @param pagesize
     * @param movementId 动态ID
     * @return 评论列表
     */
    @Override
    public List<Comment> getCommentListByMovementId(Integer page, Integer pagesize, String movementId) {
        Criteria criteria = Criteria.where("publishId").is(new ObjectId(movementId))
                .and("commentType").is(CommentType.COMMENT.getType());
        Query query = new Query(criteria)
                .skip((page - 1) * pagesize)
                .limit(pagesize)
                .with(Sort.by(Sort.Order.desc("created")));
        return this.mongoTemplate.find(query, Comment.class);
    }

    /**
     * 根据动态ID判断用户是否已经点过赞了
     *
     * @param movementId 动态ID
     * @param type       类型 这里可以是点赞，也可以是喜欢
     * @param userId     用户ID
     * @return true 点过赞 false 未点赞
     */
    @Override
    public Boolean check(String movementId, int type, Long userId) {
        // 构建条件 动态id，用户id，点赞类型
        Criteria criteria = Criteria.where("publishId").is(new ObjectId(movementId))
                .and("userId").is(userId)
                .and("commentType").is(type);
        Query query = new Query(criteria);
        return this.mongoTemplate.exists(query, Comment.class);
    }

    /**
     * 取消点赞 喜欢 评论点赞
     *
     * @param comment 要删除的评论,喜欢 评论点赞
     * @return 取消点赞之后最新的点赞数量
     */
    @Override
    public Integer delete(Comment comment, int type) {
        // 1. 解析数据
        ObjectId publishId = comment.getPublishId();
        Long userId = comment.getUserId();
        // 2. 构造条件
        Criteria criteria = Criteria.where("publishId").is(publishId)
                .and("userId").is(userId)
                .and("commentType").is(type);
        Query query = new Query(criteria);
        // 3. 删除comment中数据
        this.mongoTemplate.remove(query, Comment.class);
        if (type == CommentType.LIKECOMMENT.getType()) {
            Query modifyQuery = new Query(Criteria.where("id").is(publishId));
            Update update = new Update();
            update.inc("likeCount", -1);
            FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
            return this.mongoTemplate.findAndModify(modifyQuery, update, findAndModifyOptions, Comment.class).getLikeCount();
        } else {
            // 4. 更新相应的movement中数据
            Query modifyQuery = new Query(Criteria.where("id").is(publishId));
            Update update = new Update();
            Integer commentType = type;
            if (commentType == CommentType.LIKE.getType()) {
                update.inc("likeCount", -1);
            } else if (commentType == CommentType.COMMENT.getType()) {
                update.inc("commentCount", -1);
            } else {
                update.inc("loveCount", -1);
            }

            // 调用template更新 返回更新后的Movement对象
            FindAndModifyOptions findAndModifyOptions = FindAndModifyOptions.options().returnNew(true);
            Movement modify = this.mongoTemplate.findAndModify(modifyQuery, update, findAndModifyOptions, Movement.class);
            // 5.返回结果
            return modify.getCount(type);
        }
    }

}
