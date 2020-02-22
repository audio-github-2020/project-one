package com.itheima.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageSender {
    //JmsMessagingtemplate
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 用来发送文本消息
     * @param text
     */
    public void sendTextMessage(String text) {
        jmsMessagingTemplate.convertAndSend("UserInfo", text);
    }

    /**
     * 用来发送Map消息
     */
    public void sendMapMessage(){
        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("username","brown");
        dataMap.put("age","13");
        jmsMessagingTemplate.convertAndSend("Map",dataMap);
    }
}
