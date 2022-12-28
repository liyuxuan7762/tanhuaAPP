package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.dubbo.api.VisitorApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.mongo.Movement;
import com.tanhua.model.mongo.Visitors;
import com.tanhua.model.vo.ErrorResult;
import com.tanhua.model.vo.MovementsVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.VisitorsVo;
import com.tanhua.server.exception.BusinessException;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tanhua.common.utils.Constants.*;

@Service
public class MomentService {
    @Resource
    private OssTemplate ossTemplate;

    @DubboReference
    private MovementApi movementApi;

    @DubboReference
    private UserInfoApi userInfoApi;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @DubboReference
    private VisitorApi visitorApi;


    public void publish(Movement movement, MultipartFile[] files) throws IOException {
        if (StringUtils.isEmpty(movement.getTextContent())) {
            throw new BusinessException(ErrorResult.contentError());
        }
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
        List<MovementsVo> voList = createMovementVo(movementList);
        // 6. 构建返回值
        return new PageResult(page, pagesize, 0, voList);
    }

    private List<MovementsVo> createMovementVo(List<Movement> movementList) {
        if (CollUtil.isEmpty(movementList)) {
            // 如果为动态为空，那么说明已经到底了 那么直接返回一个空的vo集合即可
            List<MovementsVo> list = new ArrayList<>();
            return list;
        } else {
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
                    MovementsVo init = MovementsVo.init(userInfo, movement);
                    // 使用EmptyList 包报错 https://blog.csdn.net/fengbin111/article/details/105909654/
                    // 从Redis中获取数据，判断用户书否喜欢过或者点赞过这条动态
                    String key = MOVEMENTS_INTERACT_KEY + movement.getId().toHexString();
                    String loveHashKey = MOVEMENT_LOVE_HASHKEY + UserHolder.getUserId();
                    String likeHashKey = MOVEMENT_LIKE_HASHKEY + UserHolder.getUserId();
                    if (this.redisTemplate.opsForHash().hasKey(key, loveHashKey)) {
                        init.setHasLoved(1);
                    }
                    if (this.redisTemplate.opsForHash().hasKey(key, likeHashKey)) {
                        init.setHasLiked(1);
                    }
                    voList.add(init);
                }
            }
            return voList;
        }

    }

    public PageResult getRecommendMovement(Integer page, Integer pagesize) {
        // 1. 获取用户id
        Long userId = UserHolder.getUserId();
        // 2. 根据用户Id 到redis中查询数据 判断推荐信息是否存在
        String key = Constants.MOVEMENTS_RECOMMEND + userId;
        String recommendStr = redisTemplate.opsForValue().get(key);
        List<Movement> movementList = Collections.emptyList();
        if (StringUtils.isEmpty(recommendStr)) {
            // 如果推荐列表为空，则随机生成记录
            movementList = this.movementApi.getRandomRecommendMovement(pagesize);
        } else {
            // 3. 获取到pids，进行分页得到最终的动态列表
            // 解析pid
            String[] split = recommendStr.split(",");
            // 判断是否还需要分页
            if ((page - 1) * pagesize < split.length) {
                List<Long> pids = Arrays.stream(split)
                        .skip((page - 1) * pagesize)
                        .limit(pagesize)
                        .map(e -> Long.valueOf(e))
                        .collect(Collectors.toList());

                // 根据pids查询出所有的movement
                movementList = this.movementApi.getMovementByPids(pids);
            }
        }
        // 4. 封装vo
        List<MovementsVo> movementsVoList = createMovementVo(movementList);
        // 5. 封装返回值
        return new PageResult(page, pagesize, 0, movementsVoList);
    }

    public MovementsVo getMovementDetailById(String id) {
        // 1. 根据ID查询得到Movement对象
        Movement movement = this.movementApi.getMovementById(id);
        // 2. 根据Movement的userId查询到该动态发布者的详细信息
        Long userId = movement.getUserId();
        UserInfo userInfo = this.userInfoApi.getUserInfoById(userId);
        // 3. 封装Vo对象
        return MovementsVo.init(userInfo, movement);
    }

    /**
     * 查询谁看过我
     *
     * @return
     */
    public List<VisitorsVo> visitors() {
        // 1. 从Redis中获取数据 查看最后一次查看完整访客列表的时间
        // Redis中的key VISITORS 哈希key 用户id value为最后一次查看的时间戳
        String lastTime = (String) this.redisTemplate.opsForHash().get(VISITORS, UserHolder.getUserId().toString());
        Long time = lastTime != null ? Long.valueOf(lastTime) : null;
        // 2. 查询访客表
        List<Visitors> visitorsList = this.visitorApi.getVisitors(UserHolder.getUserId(), time);
        List<Long> ids = CollUtil.getFieldValues(visitorsList, "visitorUserId", Long.class);
        // 3. 查询访客的用户详情
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        // 4. 封装数据
        List<VisitorsVo> voList = new ArrayList<>();
        for (Visitors visitors : visitorsList) {
            Long visitorUserId = visitors.getVisitorUserId();
            UserInfo userInfo = map.get(visitorUserId);
            if (userInfo != null) {
                voList.add(VisitorsVo.init(userInfo, visitors));
            }
        }

        return voList;
    }
}

//java.lang.UnsupportedOperationException
//        at java.util.AbstractList.add(AbstractList.java:148)
//        at java.util.AbstractList.add(AbstractList.java:108)