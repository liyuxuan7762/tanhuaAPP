package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.RecommendUserDto;
import com.tanhua.model.vo.TodayBest;
import com.tanhua.server.service.TanhuaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/tanhua")
public class TanhuaController {

    @Resource
    private TanhuaService tanhuaService;

    /**
     * 查询今日佳人
     *
     * @return 推荐佳人对象
     */
    @GetMapping("/todayBest")
    public ResponseEntity getTodayBest() {
        // 1. 调用service方法查询今日佳人
        TodayBest vo = this.tanhuaService.getTodayBest();
        // 2. 将结果返回
        return ResponseEntity.ok(vo);
    }

    /**
     * 获取推荐列表
     *
     * @return 推荐列表分页对象
     */
    @GetMapping("/recommendation")
    public ResponseEntity recommendation(RecommendUserDto recommendUserDto) {
        // 1. 条用service获取分页结果
        recommendUserDto.setCity("");
        recommendUserDto.setAge(60);
        PageResult result = this.tanhuaService.getRecommendationList(recommendUserDto);
        // 2. 返回数据
        return ResponseEntity.ok(result);
    }

    /**
     * 查询佳人详情
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/{id}/personalInfo")
    public ResponseEntity getTodayBestById(@PathVariable(name = "id") Long userId) {
        TodayBest todayBest = this.tanhuaService.getTodayBestById(userId);
        return ResponseEntity.ok(todayBest);
    }

    /**
     * 获取陌生人问题
     * @param userId 用户id
     * @return
     */
    @GetMapping("/strangerQuestions")
    public ResponseEntity strangerQuestions(Long userId) {
        String question = this.tanhuaService.getQuestionByUserId(userId);
        return ResponseEntity.ok(question);
    }

    /**
     * 回复陌生人问题
     * @param map
     * @return
     */
    @PostMapping("/strangerQuestions")
    public ResponseEntity replyQuestion(@RequestBody Map map) {
        Long userId = Long.parseLong(map.get("userId").toString());
        String reply = map.get("reply").toString();
        this.tanhuaService.replyQuestion(userId, reply);
        return ResponseEntity.ok(null);
    }
}
