package com.itheima.test;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;

public class FaceTest {
    public static final String APP_ID = "29202058";
    public static final String API_KEY = "ptSnMDAm5LB1AsbiMidBZ4we";
    public static final String SECRET_KEY = "lAaU81AxY9BxP1ql4XdSGe2MF8ND0YK5";

    @Test
    public void testFace() {
        //设置APPID/AK/SK

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String image = "https://tanhuaossservice.oss-cn-qingdao.aliyuncs.com/2022/12/21/9dd44f73-1570-4706-afe3-20cb89d81aa6.png";
        String imageType = "URL";

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        System.out.println(res.toString(2));
        String error_code = res.get("error_code").toString();
        System.out.println(error_code);


    }
}
