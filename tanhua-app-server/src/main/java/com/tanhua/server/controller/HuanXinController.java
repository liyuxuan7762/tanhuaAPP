package com.tanhua.server.controller;

import com.tanhua.model.vo.HuanXinUserVo;
import com.tanhua.server.service.HuanXinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/huanxin")
public class HuanXinController {

    @Resource
    private HuanXinService huanXinService;

    /**
     * 查询登录用户的环信用户名和密码
     * @return
     */
    @GetMapping("/user")
    public ResponseEntity getHuanXinUser() {
        HuanXinUserVo vo = this.huanXinService.getHuanXinUser();
        return ResponseEntity.ok(vo);
    }

}
