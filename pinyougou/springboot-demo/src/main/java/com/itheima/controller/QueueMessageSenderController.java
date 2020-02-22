package com.itheima.controller;

import com.itheima.message.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/queue")
public class QueueMessageSenderController {

    @Autowired
    private MessageSender messageSender;

    /**
     * 测试发送文本消息
     *
     * @param text
     * @return
     */
    @RequestMapping(value = "/send/text")
    public String sendText(String text) {
        messageSender.sendTextMessage(text);
        return "OK";
    }

    /**
     * 测试发送Map消息
     */
    @RequestMapping(value = "/send/map")
    public String sendMap() {
        messageSender.sendMapMessage();
        return "OK";
    }

}
