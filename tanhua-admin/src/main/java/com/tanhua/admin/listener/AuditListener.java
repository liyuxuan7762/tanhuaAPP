package com.tanhua.admin.listener;

import com.tanhua.autoconfig.template.AliyunGreenTemplate;
import com.tanhua.common.utils.Constants;
import com.tanhua.dubbo.api.MovementApi;
import com.tanhua.model.mongo.Movement;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class AuditListener {
    @Resource
    private AliyunGreenTemplate aliyunGreenTemplate;

    @DubboReference
    private MovementApi movementApi;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(
                            value = "tanhua.audit.queue",
                            durable = "true"
                    ),
                    exchange = @Exchange(
                            value = Constants.AUDIT_EXCHANGE_NAME
                    ),
                    key = "audit.movement"
            )
    )
    public void auditContent(String msg) {
        // 根据动态id，查询到动态的内容和对应图片
        try {
            Movement movement = this.movementApi.getMovementById(msg);
            String textContent = movement.getTextContent();
            List<String> medias = movement.getMedias();
            // 调用阿里云进行审核
            int status = 0;
            Map<String, String> txtMap = this.aliyunGreenTemplate.greenTextScan(textContent);
            Map<String, String> imgMap = this.aliyunGreenTemplate.imageScan(medias);
            if (txtMap != null && imgMap != null) {
                String txtSuggestion = txtMap.get("suggestion");
                String imgSuggestion = imgMap.get("suggestion");
                if ("block".equals(txtSuggestion) || "block".equals(imgSuggestion)) {
                    status = 2;
                } else if ("pass".equals(txtSuggestion) && "pass".equals(imgSuggestion)) {
                    status = 1;
                }
            }
            // 更新动态的状态
            this.movementApi.updateStatus(movement.getId(), status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
