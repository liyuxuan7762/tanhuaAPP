package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MomentService {
    @Resource
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;


    public void publish(Movement movement, MultipartFile[] files) throws IOException {
        // 1. 获取用户id 设置到moment中
        Long userId = UserHolder.getUserId();
        movement.setUserId(userId);
        // 2. 保存图片文件
        List<String> imageUrlList = new ArrayList<>();
        if (files != null && files.length != 0) {
            for (MultipartFile file : files) {
                String imageUrl = this.ossTemplate.uploadFile(file.getOriginalFilename(), file.getInputStream());
                imageUrlList.add(imageUrl);
            }
        }
        // 3. 将图像文件URL设置到moment中
        movement.setMedias(imageUrlList);
        // 4. 调用api保存数据
        this.movementApi.publish(movement);
    }

    public PageResult getOwnMovement(Long userId, Integer page, Integer pagesize) {
        // 1.根据用户id查询到所有的动态
        PageResult pageResult = this.movementApi.getMovementByUserId(userId, page, pagesize);
        // 判断用户是否有动态
        List<Movement> movementList = (List<Movement>) pageResult.getItems();
        if (movementList == null || movementList.size() == 0) {
            return pageResult;
        }
        // 2.根据用户id查询用户的详情
        UserInfo userInfo = this.userInfoApi.getUserInfoById(userId);
        // 3.遍历所有的动态，封装vo对象
        List<MovementsVo> voList = new ArrayList<>();
        for (Movement movement : movementList) {
            MovementsVo vo = MovementsVo.init(userInfo, movement);
            voList.add(vo);
        }
        // 4. 封装PageResult
        pageResult.setItems(voList);
        return pageResult;
    }

    public PageResult getFriendMovement(Integer page, Integer pagesize) {
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 根据用户id到时间线表中查询，条件为friendId=userId，获取到好友动态集合
        List<Movement> movementList = this.movementApi.getFriendMovementsByUserId(userId, page, pagesize);

        // 3. 从集合中获取到发送动态的所有的好友id
        if (CollUtil.isEmpty(movementList)) {
            return new PageResult();
        }
        List<Long> friendIds = CollUtil.getFieldValues(movementList, "userId", Long.class);
        // 4. 根据id查询好友的详细信息，得到一个集合
        Map<Long, UserInfo> friendUserInfo = this.userInfoApi.getUserInfoByIds(friendIds, null);
        // 5. 遍历动态集合，封装vo
        List<MovementsVo> voList = new ArrayList<>();
        for (Movement movement : movementList) {
            // 获取到动态发布者的id
            Long friendId = movement.getUserId();
            UserInfo userInfo = friendUserInfo.get(friendId);
            if (userInfo != null) {
                voList.add(MovementsVo.init(userInfo, movement));
            }
        }
        // 6. 构建返回值
        return new PageResult(page, pagesize, 0, voList);
    }
}
