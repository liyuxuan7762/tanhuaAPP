package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.server.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/smallVideos")
public class VideoController {
    @Resource
    private VideoService videoService;

    /**
     * 发布小视频
     *
     * @param videoThumbnail 视频封面
     * @param videoFile      视频文件
     * @return
     */
    @PostMapping
    public ResponseEntity publish(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        this.videoService.save(videoThumbnail, videoFile);
        return ResponseEntity.ok(null);
    }

    /**
     * 获取小视频列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @GetMapping
    public ResponseEntity getVideos(@RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "5") Integer pagesize) {
        PageResult pageResult = this.videoService.getVideos(page, pagesize);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 关注小视频作者
     * @param userId
     * @return
     */
    @PostMapping("/{id}/userFocus")
    public ResponseEntity focus(@PathVariable(name = "id") Long userId) {
        this.videoService.focus(userId);
        return ResponseEntity.ok(null);
    }

    /**
     * 取消关注小视频作者
     * @param userId
     * @return
     */
    @PostMapping("/{id}/userUnFocus")
    public ResponseEntity unfocus(@PathVariable(name = "id") Long userId) {
        this.videoService.unfocus(userId);
        return ResponseEntity.ok(null);
    }
}
