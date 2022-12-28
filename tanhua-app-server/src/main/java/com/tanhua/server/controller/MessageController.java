package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Resource
    private MessageService messageService;

    /**
     * 根据环信id查询出对应的用户详情
     * 这一个主要是在用户聊天时显示用户的头像等信息
     *
     * @param huanxinId 环信用户名
     * @return
     */
    @GetMapping("/userinfo")
    public ResponseEntity getUserInfoByHxId(String huanxinId) {
        UserInfoVo userInfo = this.messageService.getUserInfoByHxId(huanxinId);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 添加好友
     *
     * @param userId 申请人的用户id
     * @return
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(Long userId) {
        this.messageService.contacts(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 获取联系人列表
     *
     * @param page     页号
     * @param pagesize 页大小
     * @param keyword  关键字
     * @return
     */
    @GetMapping("/contacts")
    public ResponseEntity getContactList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize,
                                         String keyword) {
        PageResult pageResult = this.messageService.getContactList(page, pagesize, keyword);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 查询公告列表
     *
     * @param page     页号
     * @param pagesize 页大小
     * @return
     */
    @GetMapping("/announcements")
    public ResponseEntity getAnnouncements(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = this.messageService.getAnnouncements(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询所有喜欢列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/loves")
    public ResponseEntity getLoves(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = this.messageService.getLoves(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询登录用户被点赞
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/likes")
    public ResponseEntity getLikes(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = this.messageService.getLikes(page, pagesize);
        return ResponseEntity.ok(result);
    }

    /**
     * 查询登录用户被评论
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping("/comments")
    public ResponseEntity getComments(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pagesize) {
        PageResult result = this.messageService.getComments(page, pagesize);
        return ResponseEntity.ok(result);
    }
}
