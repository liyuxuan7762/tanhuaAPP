package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.PageUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.FocusUserApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VideoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.FocusUser;
import com.tanhua.model.mongo.Video;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VideoVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @DubboReference
    private VideoApi videoApi;

    @Resource
    private OssTemplate ossTemplate;

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FdfsWebServer fdfsWebServer;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FocusUserApi focusUserApi;

    /**
     * 发布小视频
     *
     * @param videoThumbnail
     * @param videoFile
     * @throws IOException
     */
    public void save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            // 文件为空
            throw new BusinessException(ErrorResult.error());
        }

        // 将缩略图保存到阿里云
        String imageUrl = this.ossTemplate.uploadFile(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        // 保存视频到Fast DFS
        String ext = videoFile.getOriginalFilename().substring(videoFile.getOriginalFilename().lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), ext, null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // 创建Video对象
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setUserId(UserHolder.getUserId());
        video.setText("这是我的第一条视频");

        String id = this.videoApi.save(video);

        if (StringUtils.isEmpty(id)) {
            throw new BusinessException(ErrorResult.error());
        }
    }

    /**
     * 分页查询小视频列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    @Cacheable(
            value = "video",
            key = "T(com.tanhua.server.interceptor.UserHolder).getUserId() + '_' + #page +'_' + #pagesize"
    )
    public PageResult getVideos(Integer page, Integer pagesize) {
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 根据用户Id 到redis中查询数据 判断推荐信息是否存在
        String key = Constants.VIDEOS_RECOMMEND + userId;
        String recommendStr = redisTemplate.opsForValue().get(key);
        List<Video> videoList = new ArrayList<>();
        int redisTotalPage = 0;
        if (StringUtils.isNotEmpty(recommendStr)) {
            String[] split = recommendStr.split(",");
            // 判断是否还需要分页
            if ((page - 1) * pagesize < split.length) {
                List<Long> vids = Arrays.stream(split)
                        .skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());

                // 根据pids查询出所有的movement
                videoList = this.videoApi.getVideoByPids(vids);

            }
            redisTotalPage = PageUtil.totalPage(split.length, pagesize);
        }
        if (videoList.isEmpty()) {
            // 到MongoDB中按照时间顺序分页查找
            videoList = this.videoApi.getVideos(page - redisTotalPage, pagesize);
        }

        // 查询视频作者的详细信息
        List<Long> ids = CollUtil.getFieldValues(videoList, "userId", Long.class);
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        // 封装对象
        List<VideoVo> voList = new ArrayList<>();
        for (Video video : videoList) {
            Long id = video.getUserId();
            UserInfo userInfo = map.get(id);
            if (userInfo != null) {
                voList.add(VideoVo.init(userInfo, video));
            }
        }
        return new PageResult(page, pagesize, 0, voList);
    }

    /**
     * 关注小视频作者
     *
     * @param userId
     */
    public void focus(Long userId) {
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(userId);
        focusUser.setCreated(System.currentTimeMillis());
        this.focusUserApi.save(focusUser);

        // 保存到Redis
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(userId);

        this.redisTemplate.opsForHash().put(key, hashKey, "1");
    }

    /**
     * 取消关注小视频作者
     *
     * @param userId
     */
    public void unfocus(Long userId) {
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(userId);
        this.focusUserApi.delete(focusUser);

        // 保存到Redis
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(userId);

        this.redisTemplate.opsForHash().delete(key, hashKey);
    }
}
