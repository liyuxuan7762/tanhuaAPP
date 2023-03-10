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

import static com.tanhua.common.utils.LogOperationCodeConstants.MESSAGE_VIDEO;
import static com.tanhua.common.utils.LogOperationCodeConstants.PUBLISH_VIDEO;

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

    @Resource
    private MqMessageService mqMessageService;

    /**
     * ???????????????
     *
     * @param videoThumbnail
     * @param videoFile
     * @throws IOException
     */
    public void save(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        if (videoFile.isEmpty() || videoThumbnail.isEmpty()) {
            // ????????????
            throw new BusinessException(ErrorResult.error());
        }

        // ??????????????????????????????
        String imageUrl = this.ossTemplate.uploadFile(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        // ???????????????Fast DFS
        String ext = videoFile.getOriginalFilename().substring(videoFile.getOriginalFilename().lastIndexOf(".") + 1);
        StorePath storePath = fastFileStorageClient.uploadFile(videoFile.getInputStream(), videoFile.getSize(), ext, null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        // ??????Video??????
        Video video = new Video();
        video.setVideoUrl(videoUrl);
        video.setPicUrl(imageUrl);
        video.setUserId(UserHolder.getUserId());
        video.setText("???????????????????????????");

        String id = this.videoApi.save(video);

        if (StringUtils.isEmpty(id)) {
            throw new BusinessException(ErrorResult.error());
        }
        this.mqMessageService.sendLogService(UserHolder.getUserId(), PUBLISH_VIDEO, MESSAGE_VIDEO, id);
    }

    /**
     * ???????????????????????????
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
        // 1. ????????????id
        Long userId = UserHolder.getUserId();
        // 2. ????????????Id ???redis??????????????? ??????????????????????????????
        String key = Constants.VIDEOS_RECOMMEND + userId;
        String recommendStr = redisTemplate.opsForValue().get(key);
        List<Video> videoList = new ArrayList<>();
        int redisTotalPage = 0;
        if (StringUtils.isNotEmpty(recommendStr)) {
            String[] split = recommendStr.split(",");
            // ???????????????????????????
            if ((page - 1) * pagesize < split.length) {
                List<Long> vids = Arrays.stream(split)
                        .skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());

                // ??????pids??????????????????movement
                videoList = this.videoApi.getVideoByPids(vids);

            }
            redisTotalPage = PageUtil.totalPage(split.length, pagesize);
        }
        if (videoList.isEmpty()) {
            // ???MongoDB?????????????????????????????????
            videoList = this.videoApi.getVideos(page - redisTotalPage, pagesize);
        }

        // ?????????????????????????????????
        List<Long> ids = CollUtil.getFieldValues(videoList, "userId", Long.class);
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        // ????????????
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
     * ?????????????????????
     *
     * @param userId
     */
    public void focus(Long userId) {
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(userId);
        focusUser.setCreated(System.currentTimeMillis());
        this.focusUserApi.save(focusUser);

        // ?????????Redis
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(userId);

        this.redisTemplate.opsForHash().put(key, hashKey, "1");
    }

    /**
     * ???????????????????????????
     *
     * @param userId
     */
    public void unfocus(Long userId) {
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(UserHolder.getUserId());
        focusUser.setFollowUserId(userId);
        this.focusUserApi.delete(focusUser);

        // ?????????Redis
        String key = Constants.FOCUS_USER + UserHolder.getUserId();
        String hashKey = String.valueOf(userId);

        this.redisTemplate.opsForHash().delete(key, hashKey);
    }
}
