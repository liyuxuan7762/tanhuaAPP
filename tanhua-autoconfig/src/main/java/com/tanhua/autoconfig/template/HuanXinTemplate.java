package com.tanhua.autoconfig.template;

import cn.hutool.core.collection.CollUtil;
import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import com.tanhua.autoconfig.properties.HuanXinProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class HuanXinTemplate {
    private EMService service;

    public HuanXinTemplate(HuanXinProperties properties) {
        EMProperties emProperties = EMProperties.builder()
                .setAppkey(properties.getAppKey())
                .setClientId(properties.getClientId())
                .setClientSecret(properties.getSecretKey())
                .build();
        service = new EMService(emProperties);
    }

    //创建环信用户
    public Boolean createUser(String username,String password) {
        try {
            //创建环信用户
            service.user().create(username.toLowerCase(), password)
                    .block();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            log.error("创建环信用户失败~");
        }
        return false;
    }

    //添加联系人
    public Boolean addContact(String username1,String username2) {
        try {
            //创建环信用户
            service.contact().add(username1,username2)
                    .block();
            return true;
        }catch (Exception e) {
            log.error("添加联系人失败~");
        }
        return false;
    }

    //删除联系人
    public Boolean deleteContact(String username1,String username2) {
        try {
            //创建环信用户
            service.contact().remove(username1,username2)
                    .block();
            return true;
        }catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }

    //发送消息
    public Boolean sendMsg(String username,String content) {
        try {
            //接收人用户列表
            Set<String> set = CollUtil.newHashSet(username);
            //文本消息
            EMTextMessage message = new EMTextMessage().text(content);
            //发送消息  from：admin是管理员发送
            service.message().send("admin","users",
                    set,message,null).block();
            return true;
        }catch (Exception e) {
            log.error("删除联系人失败~");
        }
        return false;
    }
}
