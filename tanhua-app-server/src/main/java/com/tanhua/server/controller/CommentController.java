package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {
    @Resource
    private CommentService commentService;

    /**
     * 发布一条评论
     * @param map 动态ID+评论正文
     * @return
     */
    @PostMapping
    public ResponseEntity publishComment(@RequestBody Map map) {
        // 1. 解析到前端传递的评论的动态id和用户的评论正文
        String movementId = map.get("movementId").toString();
        String comment = map.get("comment").toString();
        // 2. 调用Service方法
        this.commentService.publishComment(movementId, comment);
        // 3. 构建返回值
        return ResponseEntity.ok(null);
    }

    /**
     * 根据动态ID查询到动态的所有的评论列表
     * @param page 页号
     * @param pagesize 页大小
     * @param movementId 动态ID
     * @return
     */
    @GetMapping
    public ResponseEntity getCommentListByMovementId(@RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "5") Integer pagesize,
                                                     String movementId) {
        // 1. 调用Service方法查询到CommentVO集合的PageResult
        PageResult result = this.commentService.getCommentListByMovementId(page,pagesize, movementId);
        // 2. 返回结果
        return ResponseEntity.ok(result);
    }
}
