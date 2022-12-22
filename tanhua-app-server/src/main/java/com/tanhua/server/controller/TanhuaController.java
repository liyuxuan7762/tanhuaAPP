package com.tanhua.server.controller;

import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Resource
    private TanhuaService tanhuaService;

    /**
     * 查询今日佳人
     *
     * @return
     */
    @GetMapping("/todayBest")
    public ResponseEntity getTodayBest() {
        // 1. 调用service方法查询今日佳人
        TodayBest vo = this.tanhuaService.getTodayBest();
        // 2. 将结果返回
        return ResponseEntity.ok(vo);
    }
}
