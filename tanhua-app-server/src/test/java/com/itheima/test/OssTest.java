package com.itheima.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.tanhua.autoconfig.template.OssTemplate;
import com.tanhua.server.AppServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppServerApplication.class)
public class OssTest {

    @Resource
    private OssTemplate ossTemplate;

    @Test
    public void testTemplateUpload() throws IOException {
        String path = "C:\\Users\\李宇轩\\Desktop\\1.jpg";
        InputStream inputStream = Files.newInputStream(new File(path).toPath());
        String url = ossTemplate.uploadFile(path, inputStream);
        System.out.println(url);
    }

    @Test
    public void testOss() throws FileNotFoundException {

        //1、配置图片路径
        String path = "C:\\Users\\李宇轩\\Pictures\\unsplash--BZc9Ee1qo0.png";
        //2、构造FileInputStream
        FileInputStream inputStream = new FileInputStream(new File(path));
        //3、拼写图片路径
        String filename = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                +"/"+ UUID.randomUUID().toString() + path.substring(path.lastIndexOf("."));


        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "oss-cn-qingdao.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
        String accessKeyId = "LTAI5t8eyt6zxHZuE4QTcodc";
        String accessKeySecret = "8L3khm40RRbhZGn08s3ua3Wr8rMhjf";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId,accessKeySecret);

        // 填写Byte数组。
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
        ossClient.putObject("tanhuaossservice", filename, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        String url = "https://tanhuaossservice.oss-cn-qingdao.aliyuncs.com/" + filename;
        System.out.println(url);
    }
}
