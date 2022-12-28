package com.itheima.test;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class TestDFS {

    //测试将文件上传到FastDFS文件系统中
    //从调度服务器获取，一个目标存储服务器，上传
    @Resource
    private FastFileStorageClient client;

    @Resource
    private FdfsWebServer webServer;// 获取存储服务器的请求URL

    @Test
    public void testFileUpdate() throws FileNotFoundException {
        //1、指定文件
        File file = new File("E:\\07阶段七：项目实战-探花交友(V12.5)\\01-视频\\7-17 左滑右划功能\\01-今日内容介绍.mp4");
        //2、文件上传
        StorePath path = client.uploadFile(new FileInputStream(file),
                file.length(), "mp4", null);
        //3、拼接访问路径
        String url = webServer.getWebServerUrl() + path.getFullPath();
        System.out.println(url);
    }

}
