package com.itheima.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RabbitListener(queues = "q_hello1")
public class AmqpMessageListener {
    @Autowired
    private MessageSender messageSender;

    @RabbitHandler
    public void process(String msg) throws ClientException {
        System.out.println("Receiver  : " + msg);

        //将JSON数据转换为HashMap对象
        HashMap<String, String> dataMap =
                JSON.parseObject(msg, new TypeReference<HashMap<String, String>>() {
                });

        //调用阿里大于实现短信发送
        SendSmsResponse response = messageSender.sendSms(dataMap.get("signName"),
                dataMap.get("templateCode"),
                dataMap.get("mobile"),
                dataMap.get("param"));

        System.out.println("发送消息的结果数据："+response.getMessage());

    }

}
