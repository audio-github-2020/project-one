package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/20 16:15
 *
 ****/
@RestController
@RequestMapping(value = "/message")
public class SendMessageController {

    @Autowired
    private JmsMessagingTemplate template;

    /***
     * 向MQ发送消息
     * @return
     */
    @RequestMapping(value = "/send")
    public String sendMessage() {
        //创建Map消息发送
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("signName","危宝宝可爱坊");
        dataMap.put("templateCode","SMS_184115470");
        dataMap.put("mobile","13166036810");
        dataMap.put("param","{\"code\":\""+(int)(Math.random()*10000)+"\"}");
//        dataMap.put("param","{\"code\":\"520520\"}");


        template.convertAndSend("message-list",dataMap);

        return "success";
    }


}
