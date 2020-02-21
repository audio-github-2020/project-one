package com.itheima.mq.spring;

import com.itheima.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Map;

@Component
public class MessageProducer {
    //消息发送对象JmsTemplate
    @Autowired
    private JmsTemplate jmsTemplate;

    //发送地址
    @Autowired
    private Destination destination;

    /**
     * 发送文本消息
     */
    public void sendTextMessage(final String text) {
        jmsTemplate.send(destination, new MessageCreator() {

            public Message createMessage(Session session) throws JMSException {
                //创建消息对象
                TextMessage textMessage=session.createTextMessage();
                //设置消息内容
                textMessage.setText(text);
                return textMessage;
            }
        });
    }

    /**
     * 发送Map消息
     */
    public void sendMapMessage(final Map<String,String> dataMap){
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                //创建一个MapMessage
                MapMessage mapMessage=session.createMapMessage();
                //设置键值对数据

                //假设要放入Object类型
                mapMessage.setObject("usernameObj",dataMap);
                mapMessage.setString("username","red");

                return mapMessage;
            }
        });
    }

    /**
     * 发送JavaBean类型
     *
     */
    public void sendObjectMessage(final User user){
        jmsTemplate.send(destination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                //创建一个objectMessage
                ObjectMessage objectMessage=session.createObjectMessage();
                //设置键值对数据

                //假设要放入Object类型
                objectMessage.setObject(user);

                return objectMessage;
            }
        });
    }
}
