package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.service.CommentService;
import com.tanhua.server.service.MomentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/movements")
public class MomentController {
    @Resource
    private MomentService momentService;

    @Resource
    private CommentService commentService;

    /**
     * 发送动态
     * @param movement 动态对象
     * @param imageContent 图片列表
     * @return
     * @throws IOException
     */
    @PostMapping
    public ResponseEntity publish(Movement movement, MultipartFile[] imageContent) throws IOException {
        // 0. 判断动态是否为空
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }
        // 1. 调用service方法保存动态
        this.momentService.publish(movement, imageContent);
        // 2. 返回结果
        return  ResponseEntity.ok(null);
    }

    /**
     * 查询个人动态
     * @param userId 登录用户ID
     * @param page 页码
     * @param pagesize 页大小
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity getOwnMovement(Long userId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "5") Integer pagesize) {
        // 调用service方法
        PageResult result = this.momentService.getOwnMovement(userId, page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 查看好友动态
     * @param page 页号
     * @param pagesize 页大小
     * @return
     */
    @GetMapping
    public ResponseEntity getFriendMovement(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "5") Integer pagesize) {
        PageResult result = this.momentService.getFriendMovement(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取推荐的动态
     * @param page 页号
     * @param pagesize 页大小
     * @return
     */
    @GetMapping("/recommend")
    public ResponseEntity getRecommendMovement(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "5") Integer pagesize) {
        PageResult result = this.momentService.getRecommendMovement(page,pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询动态的详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity getMovementDetail(@PathVariable(name = "id") String id) {
        if (id.equals("visitors")) {
            return ResponseEntity.ok(null);
        } else {
            // 1. 调用方法
            MovementsVo movementsVo = this.momentService.getMovementDetailById(id);
            // 2. 返回数据
            return ResponseEntity.ok(movementsVo);
        }
    }

    /**
     * 用户点赞
     * @param movementId 动态Id
     * @return 点赞之后最新的点赞数量
     */
    @GetMapping("/{id}/likeMovement")
    public ResponseEntity like(@PathVariable(name = "id") String movementId) {
        // 1. 调用Service方法完成点赞
        Integer likeCount = this.commentService.like(movementId);
        // 2. 返回结果
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 取消点赞
     * @param movementId 动态Id
     * @return 取消点赞之后最新的点赞数量
     */
    @GetMapping("/dislikeMovement/{id}")
    public ResponseEntity dislike(@PathVariable(name = "id") String movementId) {
        // 1. 调用Service方法完成点赞
        Integer likeCount = this.commentService.dislike(movementId);
        // 2. 返回结果
        return ResponseEntity.ok(likeCount);
    }

    /**
     * 用户喜欢动态
     * @param movementId 动态Id
     * @return
     */
    @GetMapping("/{id}/love")
    public ResponseEntity love(@PathVariable(name = "id") String movementId) {
        // 调用Service方法完成喜欢
        Integer count = this.commentService.love(movementId);
        return ResponseEntity.ok(count);
    }

    /**
     * 用户取消喜欢动态
     * @param movementId 动态Id
     * @return
     */
    @GetMapping("/{id}/unlove")
    public ResponseEntity unlove(@PathVariable(name = "id") String movementId) {
        // 调用Service方法完成喜欢
        Integer count = this.commentService.unlove(movementId);
        return ResponseEntity.ok(count);
    }


}
