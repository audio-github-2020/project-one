package com.itheima.test;

import com.itheima.domain.User;
import com.itheima.mq.spring.MessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-mq.xml")
public class SpringProducerTest {

    @Autowired
    private MessageProducer messageProducer;

    /**
     *
     * 文本消息测试
     */
    @Test
    public void textSendTextMessage(){
        messageProducer.sendTextMessage("Hello SpringProducer Test!"+Math.random());
    }

    /**
     * Map消息测试
     */
    @Test
    public void testSendMapMessage(){
        Map<String,String> dataMap=new HashMap<String, String>();
        dataMap.put("user","white");
        dataMap.put("age","20");
        messageProducer.sendMapMessage(dataMap);
    }

    /**
     * Object消息测试
     */
    @Test
    public void testSendObjectMessage(){
        User user = new User(123, "BROWN"+Math.random(), new Date());
        messageProducer.sendObjectMessage(user);

    }
}
