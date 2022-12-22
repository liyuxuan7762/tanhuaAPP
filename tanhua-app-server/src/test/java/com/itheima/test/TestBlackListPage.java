package com.itheima.test;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.dubbo.api.BlackListApi;
import com.tanhua.model.domain.UserInfo;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class TestBlackListPage {

    @Resource
    private BlackListApi blackListApi;

    @Test
    public void testPage() {
        IPage<UserInfo> blackList = blackListApi.getBlackList(106L, 1, 3);
        System.out.println(blackList);

    }
}
