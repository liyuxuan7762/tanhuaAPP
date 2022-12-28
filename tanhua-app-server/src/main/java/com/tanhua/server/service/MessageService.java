package com.tanhua.server.service;

import cn.hutool.core.collection.CollUtil;
import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.AnnouncementApi;
import com.tanhua.dubbo.api.CommentApi;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.Announcement;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.model.enums.CommentType;
import com.tanhua.model.mongo.Comment;
import com.tanhua.model.mongo.Friend;
import com.tanhua.model.vo.CommentVo;
import com.tanhua.model.vo.ContactVo;
import com.tanhua.model.vo.PageResult;
import com.tanhua.model.vo.UserInfoVo;
import com.tanhua.server.interceptor.UserHolder;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @DubboReference
    private UserInfoApi userInfoApi;

    @DubboReference
    private FriendApi friendApi;

    @DubboReference
    private AnnouncementApi announcementApi;

    @DubboReference
    private CommentApi commentApi;

    @Resource
    private HuanXinTemplate huanXinTemplate;

    public UserInfoVo getUserInfoByHxId(String huanxinId) {
        // 获取到用户id
        Long userId = Long.parseLong(huanxinId.substring(2));
        // 根据用户id查询用户详情
        UserInfo userInfo = userInfoApi.getUserInfoById(userId);
        UserInfoVo vo = new UserInfoVo();
        BeanUtils.copyProperties(userInfo, vo);
        if (userInfo.getAge() != null) {
            vo.setAge(userInfo.getAge().toString());
        }

        return vo;
    }

    public void contacts(Long userId) {
        // 1. 保存好友信息到环信
        this.huanXinTemplate.addContact(Constants.HX_USER_PREFIX + userId, Constants.HX_USER_PREFIX + UserHolder.getUserId());
        // 2. 保存到MongoDB
        this.friendApi.save(UserHolder.getUserId(), userId);
    }

    public PageResult getContactList(Integer page, Integer pagesize, String keyword) {
        // 1. 获取当前用户id
        Long userId = UserHolder.getUserId();
        // 2. 根据用户id在friend表中查询出所有的好友id
        List<Friend> friendList = this.friendApi.getFriendList(userId);
        List<Long> ids = CollUtil.getFieldValues(friendList, "friendId", Long.class);
        // 3. 根据好友id，查询出所有好友的用户详情
        Map<Long, UserInfo> userInfoByIds = this.userInfoApi.getUserInfoByIds(ids, null);
        // 4. 封装VO
        List<ContactVo> voList = new ArrayList<>();
        for (Friend friend : friendList) {
            Long friendId = friend.getFriendId();
            UserInfo userInfo = userInfoByIds.get(friendId);
            if (userInfo != null) {
                voList.add(ContactVo.init(userInfo));
            }
        }
        // 5. 封装实现类
        return new PageResult(page, pagesize, 0, voList);
    }

    public PageResult getAnnouncements(Integer page, Integer pagesize) {
        List<Announcement> announcements = this.announcementApi.getAnnouncements(page, pagesize);
        return new PageResult(page, pagesize, 0, announcements);
    }

    public PageResult getLoves(Integer page, Integer pagesize) {
        return getCommentByType(UserHolder.getUserId(), page, pagesize, CommentType.LOVE.getType());
    }

    public PageResult getLikes(Integer page, Integer pagesize) {
        return getCommentByType(UserHolder.getUserId(), page, pagesize, CommentType.LIKE.getType());
    }

    public PageResult getComments(Integer page, Integer pagesize) {
        return getCommentByType(UserHolder.getUserId(), page, pagesize, CommentType.COMMENT.getType());
    }

    private PageResult getCommentByType(Long userId, Integer page, Integer pagesize, int type) {
        List<Comment> commentList = this.commentApi.getCommentByType(userId, page, pagesize, type);
        List<Long> ids = CollUtil.getFieldValues(commentList, "userId", Long.class);
        Map<Long, UserInfo> map = this.userInfoApi.getUserInfoByIds(ids, null);
        List<CommentVo> voList = new ArrayList<>();
        for (Comment comment : commentList) {
            Long id = comment.getUserId();
            UserInfo userInfo = map.get(id);
            if (userInfo != null) {
                voList.add(CommentVo.init(userInfo, comment));
            }
        }
        return new PageResult(page,pagesize, 0, voList);
    }
}
