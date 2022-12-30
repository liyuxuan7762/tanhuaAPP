package com.tanhua.admin.controller;

import com.tanhua.admin.service.ManageService;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/manage")
public class ManageController {
    @Resource
    private ManageService manageService;

    /**
     * 查询用户列表
     *
     * @return
     */
    @GetMapping("/users")
    public ResponseEntity users(@RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "20") Integer pagesize) {
        PageResult result = this.manageService.getUsers(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户详情
     *
     * @param userId
     * @return
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity getUserByUserId(@PathVariable(name = "userId") Long userId) {
        UserInfo userInfo = this.manageService.getUserByUserId(userId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 查询指定用户的视频列表
     *
     * @param page
     * @param pagesize
     * @param uid
     * @return
     */
    @GetMapping("/videos")
    public ResponseEntity getVideosByUserId(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "20") Integer pagesize,
                                            Long uid) {
        PageResult result = this.manageService.getVideosByUserId(page, pagesize, uid);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询指定用户的所有动态列表
     *
     * @param page
     * @param pagesize
     * @param uid
     * @param state
     * @return
     */
    @GetMapping("/messages")
    public ResponseEntity getMovementByUserId(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "20") Integer pagesize,
                                              Long uid, Integer state) {
        PageResult result = this.manageService.getMovementByUserId(page, pagesize, uid, state);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询动态详情
     *
     * @param movementId
     * @return
     */
    @GetMapping("/messages/{id}")
    public ResponseEntity getMovementDetailById(@PathVariable(name = "id") Long movementId) {
        MovementsVo vo = this.manageService.getMovementDetailById(movementId);
        return ResponseEntity.ok(vo);
    }

    @GetMapping("/messages/comments")
    public ResponseEntity getCommentsByMovementId(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "20") Integer pagesize,
                                                  Long messageID) {
        PageResult result = this.manageService.getCommentsByMovementId(page, pagesize, messageID);
        return ResponseEntity.ok(result);
    }

    /**
     * 冻结用户
     * @param map
     * @return
     */
    @PostMapping("/users/freeze")
    public ResponseEntity freeze(@RequestBody Map map) {
        Map retMap = this.manageService.freeze(map);
        return ResponseEntity.ok(retMap);
    }

    /**
     * 解冻用户
     * @param map
     * @return
     */
    @PostMapping("/users/unfreeze")
    public ResponseEntity unfreeze(@RequestBody Map map) {
        Map retMap = this.manageService.unfreeze(map);
        return ResponseEntity.ok(retMap);
    }

}
