package com.tanhua.server.service;

import com.tanhua.autoconfig.template.AipFaceTemplate;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
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

    public void uploadAvatar(Long id, MultipartFile headPhoto) {
        String imageUrl = this.verifyFace(headPhoto);
        // 3. 更新用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAvatar(imageUrl);
        this.userInfoApi.update(userInfo);
    }

    public UserInfoVo getUserInfoById(Long userID) {
        UserInfo userInfo = this.userInfoApi.getUserInfoById(userID);
        UserInfoVo userInfoVo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, userInfoVo); // 只会拷贝名字相同且类型相同的属性
        userInfoVo.setAge(userInfo.getAge().toString());
        return userInfoVo;
    }

    public void updateUserInfo(UserInfo userInfo) {
        this.userInfoApi.update(userInfo);
    }

    /**
     * 更新用户头像
     *
     * @param headPhoto
     */
    public void updateHeader(MultipartFile headPhoto) {
        // 1. 验证头像是否合法
        String imageUrl = this.verifyFace(headPhoto);
        // 2. 将信息保存到UserInfo表中
        UserInfo userInfo = new UserInfo();
        userInfo.setId(UserHolder.getUserId());
        userInfo.setAvatar(imageUrl);
        this.userInfoApi.update(userInfo);
    }

    /**
     * 验证人脸是否合法
     *
     * @param headPhoto
     * @return
     */
    private String verifyFace(MultipartFile headPhoto) {
        // 1. 将文件上传到阿里云OSS
        String imageUrl = null;
        try {
            imageUrl = ossTemplate.uploadFile(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        } catch (IOException e) {
            throw new BusinessException(ErrorResult.error());
        }
        // 2. 调用百度人脸识别判断是否是人脸，如果不是抛出异常
        boolean check = aipFaceTemplate.faceCheck(imageUrl);
        if (false) {
            throw new BusinessException(ErrorResult.faceError());
        }
        return imageUrl;
    }
}
