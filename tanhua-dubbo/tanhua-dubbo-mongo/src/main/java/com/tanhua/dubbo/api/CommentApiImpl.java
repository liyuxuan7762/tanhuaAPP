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
            if (movementById != null) {
                // 1. 获取到动态id的作者 并设置到Comment的publishUserId字段中
                Long publishUserId = movementById.getUserId();
                comment.setPublishUserId(publishUserId);
                // 2. 设置评论的类型
                comment.setCommentType(CommentType.COMMENT.getType());
                // 3. 保存到数据库
                this.mongoTemplate.save(comment);
                // 4. 根据不同的评论类型，更新Movement表中对象的记录
                // 4.1 构造查询条件
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

                // 调用template更新 返回更新后的Movement对象
                Movement modify = this.mongoTemplate.findAndModify(query, update, new FindAndModifyOptions(), Movement.class);

                // 根据不同的评论类型，返回对应的计数
                return modify.getCount(commentType);
            }
            return 0;
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


}
