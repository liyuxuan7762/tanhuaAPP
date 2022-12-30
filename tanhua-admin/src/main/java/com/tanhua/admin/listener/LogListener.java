package com.tanhua.admin.listener;

import com.alibaba.fastjson.JSON;
import com.tanhua.admin.mapper.LogMapper;
import com.tanhua.common.utils.Constants;
import com.tanhua.model.domain.Log;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
public class LogListener {

    @Resource
    private LogMapper logMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    // 设置队列名称
                    value = @Queue(value = "tanhua.log.queue", durable = "true"),
                    // 设置交换机
                    exchange = @Exchange(
                            value = Constants.LOG_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC
                    ),
                    // 设置路由键
                    key = "log.*"
            )
    )
    public void saveLogs(String msg) {
        // 解析消息
        Map map = JSON.parseObject(msg, Map.class);
        long userId = Long.parseLong(map.get("userId").toString());
        String logTime = map.get("logTime").toString();
        String type = map.get("type").toString();
        Log log = new Log(userId, logTime, type);
        this.logMapper.insert(log);
    }
}
