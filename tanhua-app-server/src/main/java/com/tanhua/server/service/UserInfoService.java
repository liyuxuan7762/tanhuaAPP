package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class UserInfoService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @Resource
    private OssTemplate ossTemplate;

    @Resource
    private AipFaceTemplate aipFaceTemplate;

    public void save(UserInfo userInfo) {
        this.userInfoApi.save(userInfo);
    }

    public void uploadAvatar(Integer id, MultipartFile headPhoto) {
        // 1. 将文件上传到阿里云OSS
        String imageUrl = null;
        try {
            imageUrl = ossTemplate.uploadFile(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 2. 调用百度人脸识别判断是否是人脸，如果不是抛出异常
        boolean check = aipFaceTemplate.faceCheck(imageUrl);
        if (!check) {
            throw new RuntimeException("头像不合法");
        }
        // 3. 更新用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(id));
        userInfo.setAvatar(imageUrl);
        this.userInfoApi.update(userInfo);
    }
}
