package com.tanhua.server.controller;

import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.SettingsVo;
import com.tanhua.server.interceptor.UserHolder;
import com.tanhua.server.service.SettingsService;
import com.tanhua.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class SettingsController {
    @Resource
    private SettingsService settingsService;

    @Resource
    private UserService userService;

    /**
     * 获取用户的设置信息
     *
     * @return
     */
    @GetMapping("/settings")
    public ResponseEntity getSettings() {
        // 1. 调用service查询设置信息，返回VO对象
        SettingsVo settingsVo = this.settingsService.getSettings();
        // 2. 返回结果
        return ResponseEntity.ok(settingsVo);
    }

    /**
     * 保存或更新陌生人问题
     *
     * @param map
     * @return
     */
    @PostMapping("/questions")
    public ResponseEntity saveOrUpdateQuestion(@RequestBody Map map) {
        // 1. 获取参数
        String content = map.get("content").toString();
        // 2. 调用service
        this.settingsService.saveOrUpdateQuestion(content);
        // 3. 返回结果
        return ResponseEntity.ok(null);
    }

    /**
     * 更改通知设置
     *
     * @param map
     * @return
     */
    @PostMapping("notifications/setting")
    public ResponseEntity saveOrUpdateSettings(@RequestBody Map map) {
        // 1. 调用service
        this.settingsService.saveOrUpdateSettings(map);
        // 2. 返回数据
        return ResponseEntity.ok(null);
    }

    /**
     * 分页查询当前那用户的黑名单
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/blacklist")
    public ResponseEntity getBlackList(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        // 1. 调用service查询
        PageResult pageResult = this.settingsService.getBlackList(page, size);
        // 2. 返回结果
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 移除黑名单
     * @param uid
     * @return
     */
    @DeleteMapping("/blacklist/{uid}")
    public ResponseEntity removeUserFromBlackList(@PathVariable(name = "uid") Long uid) {
        // 1.调用service方法移除童虎
        this.settingsService.removeUserFromBlackList(uid);
        return ResponseEntity.ok(null);
    }

    /**
     * 修改手机号发送验证码
     * @return
     */
    @PostMapping("/phone/sendVerificationCode")
    public ResponseEntity sendVerificationCode() {
        // 1. 获取登录用户的手机号
        String userPhone = UserHolder.getUserPhone();
        // 2. 调用Service方法
        this.userService.sendMsg(userPhone);
        // 3. 返回结果
        return ResponseEntity.ok(null);
    }

    /**
     * 判断用户输入的验证码是否正确
     * @param map 验证码
     * @return 返回布尔类型，true - 正确 false - 错误
     */
    @PostMapping("/phone/checkVerificationCode")
    public ResponseEntity checkVerificationCode(@RequestBody Map map) {
        String code = map.get("verificationCode").toString();
        String userPhone = UserHolder.getUserPhone();
        boolean verification = this.userService.checkVerificationCode(code, userPhone);
        return ResponseEntity.ok(false);
    }

    /**
     * 更改手机号
     * @param phone 新手机号
     * @return 空
     */
    @PostMapping("/phone")
    public ResponseEntity updatePhone(String phone) {
        Long userId = UserHolder.getUserId();
        this.userService.updatePhone(phone, userId);
        return ResponseEntity.ok(null);
    }

}
