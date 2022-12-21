package com.tanhua.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来封装一些异常错误的返回值
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResult {

    private String errCode = "999999";
    private String errMessage;

    public static ErrorResult error() {
        return ErrorResult.builder().errCode("999999").errMessage("系统异常稍后再试").build();
    }

    public static ErrorResult fail() {
        return ErrorResult.builder().errCode("000001").errMessage("发送验证码失败").build();
    }

    public static ErrorResult loginError() {
        return ErrorResult.builder().errCode("000002").errMessage("验证码失效").build();
    }

    public static ErrorResult faceError() {
        return ErrorResult.builder().errCode("000003").errMessage("图片非人像，请重新上传!").build();
    }

    public static ErrorResult mobileError() {
        return ErrorResult.builder().errCode("000004").errMessage("手机号码已注册").build();
    }

    public static ErrorResult contentError() {
        return ErrorResult.builder().errCode("000005").errMessage("动态内容为空").build();
    }

    public static ErrorResult likeError() {
        return ErrorResult.builder().errCode("000006").errMessage("用户已点赞").build();
    }

    public static ErrorResult disLikeError() {
        return ErrorResult.builder().errCode("000007").errMessage("用户未点赞").build();
    }

    public static ErrorResult loveError() {
        return ErrorResult.builder().errCode("000008").errMessage("用户已喜欢").build();
    }

    public static ErrorResult disloveError() {
        return ErrorResult.builder().errCode("000009").errMessage("用户未喜欢").build();
    }
}