package com.tanhua.dubbo.api;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserInfoMapper;
import com.tanhua.model.domain.UserInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@DubboService
public class UserInfoApiImpl implements UserInfoApi {
    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        this.userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        this.userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo getUserInfoById(Long userID) {
        return this.userInfoMapper.selectById(userID);
    }

    @Override
    public Map<Long, UserInfo> getUserInfoByIds(List<Long> ids, UserInfo condition) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        // 1. 根据ids 使用in批量查询所有的的UserInfo
        queryWrapper.in("id", ids);
        // 2. 构建筛选条件
        if (condition != null) {
            // 性别
            if (StringUtils.isNotEmpty(condition.getGender())) {
                queryWrapper.eq("gender", condition.getGender());
            }
            // 年龄
            if (condition.getAge() != null) {
                queryWrapper.lt("age", condition.getAge());
            }

            // 学历
            if (StringUtils.isNotEmpty(condition.getEducation())) {
                queryWrapper.eq("education", condition.getEducation());
            }
            // 昵称
            if (StringUtils.isNotEmpty(condition.getNickname())) {
                queryWrapper.like("nickname", condition.getNickname());
            }

        }
        // 3. 查询
        List<UserInfo> list = this.userInfoMapper.selectList(queryWrapper);

        // 4. 使用工具类，生成Map，key为id，value为UserInfo对象
        return CollUtil.fieldValueMap(list, "id");
    }


}
