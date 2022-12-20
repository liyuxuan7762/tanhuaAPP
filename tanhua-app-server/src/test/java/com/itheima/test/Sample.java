package com.itheima.test;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;

public class Sample {


    public static void main(String[] args_) throws Exception {

        String accessKeyId = "LTAI4GKgob9vZ53k2SZdyAC7";
        String accessKeySecret= "LHLBvXmILRoyw0niRSBuXBZewQ30la";

        //配置阿里云
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";

        com.aliyun.dysmsapi20170525.Client client =  new com.aliyun.dysmsapi20170525.Client(config);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers("18618412321")
                .setSignName("物流云商")
                .setTemplateCode("SMS_205134115")
                .setTemplateParam("{\"code\":\"1234\"}");
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse response = client.sendSms(sendSmsRequest);

        SendSmsResponseBody body = response.getBody();

    }
}