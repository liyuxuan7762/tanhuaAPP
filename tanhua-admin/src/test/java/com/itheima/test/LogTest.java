package com.itheima.test;

import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.model.domain.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Resource
    private LogMapper logMapper;

    private String logTime = "";

    //模拟登录数据
    public void testInsertLoginLog() {
        for (int i = 0; i < 5; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            log.setType("0101");
            logMapper.insert(log);
        }
    }

    //模拟注册数据
    public void testInsertRegistLog() {
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            log.setType("0102");
            logMapper.insert(log);
        }
    }

    //模拟其他操作
    public void testInsertOtherLog() {
        String[] types = new String[]{"0201", "0202", "0203", "0204", "0205", "0206", "0207", "0301", "0302", "0303", "0304"};
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(logTime);
            int index = new Random().nextInt(10);
            log.setType(types[index]);
            logMapper.insert(log);
        }
    }

    @Test
    public void generData() {
        testInsertLoginLog();
        testInsertRegistLog();
        testInsertOtherLog();
    }
}