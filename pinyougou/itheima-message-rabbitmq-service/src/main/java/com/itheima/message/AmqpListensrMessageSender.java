package com.itheima.message;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Service
public class AmqpListensrMessageSender {

    @Autowired
    private MessageSender messageSender;

    //具体执行业务的方法
    @RequestMapping(produces = {"application/json;charset=UTF-8"})
    public void listen(String msg) throws ClientException {
        System.out.println(msg);
        //能够接受JSON数据，但无法实现短信发送功能

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
