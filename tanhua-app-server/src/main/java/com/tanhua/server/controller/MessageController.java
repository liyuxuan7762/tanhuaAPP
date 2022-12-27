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
     * @param userId 申请人的用户id
     * @return
     */
    @PostMapping("/contacts")
    public ResponseEntity contacts(Long userId) {
        this.messageService.contacts(userId);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/contacts")
    public ResponseEntity getContactList(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pagesize,
                                         String keyword) {
        PageResult pageResult = this.messageService.getContactList(page, pagesize, keyword);
        return ResponseEntity.ok(pageResult);
    }
}
