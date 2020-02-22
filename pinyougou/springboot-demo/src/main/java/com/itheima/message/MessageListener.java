package com.itheima.message;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageListener {

    /**
     * 消息监听
     * destination指定了要读取消息的队列
     */
    @JmsListener(destination = "UserInfo")
    public void readTextMessage(String text){
        System.out.println("读取的text是："+text);
    }

    /**
     * 读取Map消息
     */
    @JmsListener(destination = "Map")
    public void readMapMessage(Map<String,String> dataMap){
        System.out.println("读取的map是："+dataMap);
    }

}
