package com.tanhua.server.controller;

import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.exception.BusinessException;
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

    @GetMapping("/all")
    public ResponseEntity getOwnMovement(Long userId,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "5") Integer pagesize) {
        // 调用service方法
        PageResult result = this.momentService.getOwnMovement(userId, page, pagesize);
        return ResponseEntity.ok(result);
    }
}
