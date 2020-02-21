package com.itheima.mq.spring;

import com.itheima.domain.User;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;

@Component
public class SpringMessageListener implements MessageListener {


    public void onMessage(Message message) {

        //文本消息类型
        if (message instanceof TextMessage) {
            //将消息强转
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println("通过SpringMessageListener监听到的消息："+textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        //map消息类型
        if (message instanceof MapMessage) {
            //将消息强转
            MapMessage mapMessage = (MapMessage) message;
            try {
                Map<String,String> dataMap = (Map<String,String>)mapMessage.getObject("usernameObj");
                String data= (String)mapMessage.getObject("username");

                System.out.println("通过SpringMessageListener监听到的usernameObj消息："+dataMap);
                System.out.println("通过SpringMessageListener监听到的username消息："+data);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }


        //object消息类型
        if (message instanceof ObjectMessage) {
            //将消息强转
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                User user = (User) objectMessage.getObject();
                System.out.println("通过SpringMessageListener监听到的user消息："+user);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }


}
