package com.itheima.test;

import com.tanhua.server.AppServerApplication;
import com.tanhua.server.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 业务测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class BusinessTest {

    @Resource
    private CommentService commentService;

    @Test
    public void testPublishComment() {
        String movementId = "5e82dc3e6401952928c211a3";
        String comment = "测试评论测试评论";
        this.commentService.publishComment(movementId, comment);
    }
}
