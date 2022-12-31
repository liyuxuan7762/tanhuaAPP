package com.tanhua.autoconfig.template;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.green.model.v20180509.ImageSyncScanRequest;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.tanhua.autoconfig.properties.GreenProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author: itheima
 * @create: 2021-05-31 00:46
 */
@Slf4j
public class AliyunGreenTemplate {

    private IAcsClient client;

    private GreenProperties greenProperties;

    public AliyunGreenTemplate(GreenProperties greenProperties) {
        this.greenProperties = greenProperties;
        try {
            IClientProfile profile = DefaultProfile
                    .getProfile("cn-shanghai", greenProperties.getAccessKeyID(), greenProperties.getAccessKeySecret());
            DefaultProfile
                    .addEndpoint("cn-shanghai", "cn-shanghai", "Green", "green.cn-shanghai.aliyuncs.com");
            client = new DefaultAcsClient(profile);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Green配置缺失，请补充!");
        }
    }


    /**
     * 阿里云文本内容检查
     *
     * @param content
     * @return map  key - suggestion内容
     * pass：文本正常，可以直接放行，
     * review：文本需要进一步人工审核，
     * block：文本违规，可以直接删除或者限制公开
     * value -   通过，或 出错原因
     * @throws Exception
     */
    public Map<String, String> greenTextScan(String content) throws Exception {
        TextScanRequest textScanRequest = new TextScanRequest();
        textScanRequest.setAcceptFormat(FormatType.JSON); // 指定api返回格式
        textScanRequest.setHttpContentType(FormatType.JSON);
        textScanRequest.setMethod(MethodType.POST); // 指定请求方法
        textScanRequest.setEncoding("UTF-8");
        textScanRequest.setRegionId("cn-shanghai");
        List<Map<String, Object>> tasks = new ArrayList<>();
        Map<String, Object> task1 = new LinkedHashMap<>();
        task1.put("dataId", UUID.randomUUID().toString());
        /**
         * 待检测的文本，长度不超过10000个字符
         */
        task1.put("content", content);
        tasks.add(task1);
        JSONObject data = new JSONObject();

        /**
         * 检测场景，文本垃圾检测传递：antispam
         **/
        data.put("scenes", Arrays.asList("antispam"));
        data.put("tasks", tasks);
        log.info("检测任务内容：{}", JSON.toJSONString(data, true));
        textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
        // 请务必设置超时时间
        textScanRequest.setConnectTimeout(3000);
        textScanRequest.setReadTimeout(6000);

//        返回结果内容
        Map<String, String> resultMap = new HashMap<>();
        try {
            HttpResponse httpResponse = client.doAction(textScanRequest);
            if (!httpResponse.isSuccess()) {
                new RuntimeException("阿里云文本内容检查出现异常！");
            }
            JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
            log.info("检测结果内容：{}", JSON.toJSONString(scrResponse, true));
            if (200 != scrResponse.getInteger("code")) {
                new RuntimeException("阿里云文本内容检查出现异常！");
            }
            JSONArray taskResults = scrResponse.getJSONArray("data");
            for (Object taskResult : taskResults) {
                if (200 != ((JSONObject) taskResult).getInteger("code")) {
                    new RuntimeException("阿里云文本内容检查出现异常！");
                }
                JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                for (Object sceneResult : sceneResults) {
                    String scene = ((JSONObject) sceneResult).getString("scene");
                    String label = ((JSONObject) sceneResult).getString("label");
                    String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                    log.info("最终内容检测结果，suggestion = {}，label={}", suggestion, label);
//                    设置默认错误返回内容
                    resultMap.put("suggestion", suggestion);
                    if (suggestion.equals("review")) {
                        resultMap.put("reson", "文章内容中有不确定词汇");
                        log.info("返回结果，resultMap={}", resultMap);
                        return resultMap;
                    } else if (suggestion.equals("block")) {
                        String reson = "文章内容中有敏感词汇";
                        if (label.equals("spam")) {
                            reson = "文章内容中含垃圾信息";
                        } else if (label.equals("ad")) {
                            reson = "文章内容中含有广告";
                        } else if (label.equals("politics")) {
                            reson = "文章内容中含有涉政";
                        } else if (label.equals("terrorism")) {
                            reson = "文章内容中含有暴恐";
                        } else if (label.equals("abuse")) {
                            reson = "文章内容中含有辱骂";
                        } else if (label.equals("porn")) {
                            reson = "文章内容中含有色情";
                        } else if (label.equals("flood")) {
                            reson = "文章内容灌水";
                        } else if (label.equals("contraband")) {
                            reson = "文章内容违禁";
                        } else if (label.equals("meaningless")) {
                            reson = "文章内容无意义";
                        }
                        resultMap.put("reson", reson);
                        log.info("返回结果，resultMap={}", resultMap);
                        return resultMap;
                    }

                }
            }
            resultMap.put("suggestion", "pass");
            resultMap.put("reson", "检测通过");

        } catch (Exception e) {
            log.error("阿里云文本内容检查出错！");
            e.printStackTrace();
            new RuntimeException("阿里云文本内容检查出错！");
        }
        log.info("返回结果，resultMap={}", resultMap);
        return resultMap;
    }

    /**
     * 阿里云图片内容安全
     */
    public Map imageScan(List<String> imageList) throws Exception {
        IClientProfile profile = DefaultProfile
                .getProfile("cn-shanghai", greenProperties.getAccessKeyID(), greenProperties.getAccessKeySecret());
        ImageSyncScanRequest imageSyncScanRequest = new ImageSyncScanRequest();
        // 指定api返回格式
        imageSyncScanRequest.setAcceptFormat(FormatType.JSON);
        // 指定请求方法
        imageSyncScanRequest.setMethod(MethodType.POST);
        imageSyncScanRequest.setEncoding("utf-8");
        //支持http和https
        imageSyncScanRequest.setProtocol(ProtocolType.HTTP);
        JSONObject httpBody = new JSONObject();
        /**
         * 设置要检测的场景, 计费是按照该处传递的场景进行
         * 一次请求中可以同时检测多张图片，每张图片可以同时检测多个风险场景，计费按照场景计算
         * 例如：检测2张图片，场景传递porn、terrorism，计费会按照2张图片鉴黄，2张图片暴恐检测计算
         * porn: porn表示色情场景检测
         */

        httpBody.put("scenes", Arrays.asList(greenProperties.getScenes().split(",")));

        /**
         * 如果您要检测的文件存于本地服务器上，可以通过下述代码片生成url
         * 再将返回的url作为图片地址传递到服务端进行检测
         */
        /**
         * 设置待检测图片， 一张图片一个task
         * 多张图片同时检测时，处理的时间由最后一个处理完的图片决定
         * 通常情况下批量检测的平均rt比单张检测的要长, 一次批量提交的图片数越多，rt被拉长的概率越高
         * 这里以单张图片检测作为示例, 如果是批量图片检测，请自行构建多个task
         */
        List list = new ArrayList();
        for (String imageUrl : imageList) {
            JSONObject task = new JSONObject();
            task.put("dataId", UUID.randomUUID().toString());
            // 设置图片链接。
            task.put("url", imageUrl);
            task.put("time", new Date());
            list.add(task);
        }

        httpBody.put("tasks",list);

        imageSyncScanRequest.setHttpContent(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(httpBody.toJSONString()),
                "UTF-8", FormatType.JSON);
        /**
         * 请设置超时时间, 服务端全链路处理超时时间为10秒，请做相应设置
         * 如果您设置的ReadTimeout小于服务端处理的时间，程序中会获得一个read timeout异常
         */
        imageSyncScanRequest.setConnectTimeout(3000);
        imageSyncScanRequest.setReadTimeout(10000);
        HttpResponse httpResponse = null;
        try {
            httpResponse = client.doAction(imageSyncScanRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, String> resultMap = new HashMap<>();

        //服务端接收到请求，并完成处理返回的结果
        if (httpResponse != null && httpResponse.isSuccess()) {
            JSONObject scrResponse = JSON.parseObject(org.apache.commons.codec.binary.StringUtils.newStringUtf8(httpResponse.getHttpContent()));
            System.out.println(JSON.toJSONString(scrResponse, true));
            int requestCode = scrResponse.getIntValue("code");
            //每一张图片的检测结果
            JSONArray taskResults = scrResponse.getJSONArray("data");
            if (200 == requestCode) {
                for (Object taskResult : taskResults) {
                    //单张图片的处理结果
                    int taskCode = ((JSONObject) taskResult).getIntValue("code");
                    //图片要检测的场景的处理结果, 如果是多个场景，则会有每个场景的结果
                    JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
                    if (200 == taskCode) {
                        for (Object sceneResult : sceneResults) {
                            String scene = ((JSONObject) sceneResult).getString("scene");
                            String label = ((JSONObject) sceneResult).getString("label");
                            String suggestion = ((JSONObject) sceneResult).getString("suggestion");
                            //根据scene和suggetion做相关处理
                            //do something
                            System.out.println("scene = [" + scene + "]");
                            System.out.println("suggestion = [" + suggestion + "]");
                            System.out.println("suggestion = [" + label + "]");
                            if (!suggestion.equals("pass")) {
                                resultMap.put("suggestion", suggestion);
                                resultMap.put("label", label);
                                return resultMap;
                            }
                        }

                    } else {
                        //单张图片处理失败, 原因视具体的情况详细分析
                        log.error("task process fail. task response:" + JSON.toJSONString(taskResult));
                        return null;
                    }
                }
                resultMap.put("suggestion", "pass");
                return resultMap;
            } else {
                /**
                 * 表明请求整体处理失败，原因视具体的情况详细分析
                 */
                log.error("the whole image scan request failed. response:" + JSON.toJSONString(scrResponse));
                return null;
            }
        }
        return null;
    }
}