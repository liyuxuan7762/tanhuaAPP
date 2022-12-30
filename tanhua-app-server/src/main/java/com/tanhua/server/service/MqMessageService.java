package com.tanhua.server.service;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.tanhua.common.utils.Constants.AUDIT_EXCHANGE_NAME;
import static com.tanhua.common.utils.Constants.LOG_EXCHANGE_NAME;

@Service
public class MqMessageService {

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 发送日志消息
     * @param userId 操作用户id
     * @param type 操作类型
     * @param key 路由键
     * @param busId 业务id 比如如果查询动态id为1的数据，那么这个就是1
     */
    //发送日志消息
    public void sendLogService(Long userId,String type,String key,String busId) {
        try {
            Map map = new HashMap();
            map.put("userId",userId.toString());
            map.put("type",type);
            map.put("logTime",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            map.put("busId",busId);
            String message = JSON.toJSONString(map);
            amqpTemplate.convertAndSend(LOG_EXCHANGE_NAME,
                    "log."+key,message);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    //发送动态审核消息
    public void sendAudiService(String movementId) {
        try {
            amqpTemplate.convertAndSend(AUDIT_EXCHANGE_NAME,
                    "audit.movement",movementId);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }
}