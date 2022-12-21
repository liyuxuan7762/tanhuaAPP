package com.tanhua.autoconfig.template;

import com.baidu.aip.face.AipFace;
import com.tanhua.autoconfig.properties.AipFaceProperties;
import org.json.JSONObject;

import java.util.HashMap;

public class AipFaceTemplate {

    private AipFaceProperties aipFaceProperties;

    public AipFaceTemplate(AipFaceProperties aipFaceProperties) {
        this.aipFaceProperties = aipFaceProperties;
    }

    /**
     * 识别图像中是否包含人脸
     *
     * @param imageUrl 图像URL地址
     * @return
     */
    public boolean faceCheck(String imageUrl) {

        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        options.put("liveness_control", "LOW");
        // 初始化一个AipFace
        AipFace client = new AipFace(aipFaceProperties.getAppId(), aipFaceProperties.getApiKey(), aipFaceProperties.getSecretKey());

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 调用接口
        String image = "https://tanhuaossservice.oss-cn-qingdao.aliyuncs.com/2022/12/21/9dd44f73-1570-4706-afe3-20cb89d81aa6.png";
        String imageType = "URL";

        // 人脸检测
        JSONObject res = client.detect(image, imageType, options);
        return "0".equals(res.get("error_code").toString()) ? true : false;
    }
}
