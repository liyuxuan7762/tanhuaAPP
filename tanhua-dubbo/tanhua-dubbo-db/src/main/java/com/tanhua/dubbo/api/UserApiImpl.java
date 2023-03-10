package com.tanhua.dubbo.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.UserMapper;
import com.tanhua.model.domain.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class UserApiImpl implements UserApi {

    @Autowired
    private UserMapper userMapper;

    //根据手机号码查询用户
    @Override
    public User findByMobile(String mobile) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        qw.eq("mobile", mobile);
        return userMapper.selectOne(qw);
    }

    @Override
    public Long save(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public void updatePhone(String phone, Long userId) {
        User user = new User();
        user.setId(userId);
        user.setMobile(phone);
        this.userMapper.updateById(user);
    }

    @Override
    public void updateHx(User user) {
        this.userMapper.updateById(user);
    }

    @Override
    public User getHuanXinUser(Long userId) {
        return this.userMapper.selectById(userId);
    }

}
