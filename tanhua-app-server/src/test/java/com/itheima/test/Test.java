package com.itheima.test;

import com.tanhua.autoconfig.template.HuanXinTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.FriendApi;
import com.tanhua.dubbo.api.UserInfoApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class Test {

    @Resource
    private UserInfoApi userInfoApi;

    @Resource
    private HuanXinTemplate huanXinTemplate;

    @Resource
    private FriendApi friendApi;

    @org.junit.Test
    public void test() {
        String s = DigestUtils.md5Hex("123456");
        System.out.println(s);
    }

    @org.junit.Test
    public void testMP() {
        List<Long> ids = new ArrayList<>();
        ids.add(444L);
        ids.add(555L);
        // selectList() 如果查询条件不满足，则返回的list是一个size=0的集合
        Map<Long, UserInfo> userInfoByIds = this.userInfoApi.getUserInfoByIds(ids, null);
    }

    @org.junit.Test
    public void testFriend() {
        this.huanXinTemplate.addContact(Constants.HX_USER_PREFIX + 12, Constants.HX_USER_PREFIX + 106);
        // 2. 保存到MongoDB
        this.friendApi.save(106L, 12L);
    }
}
