package com.pinyougou.manage.service;

import com.pinyougou.mq.MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class MessageSender {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination destination;

    public void sendObjectMessage(MessageInfo messageInfo) {
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                //封装数据
                objectMessage.setObject(messageInfo);
                return objectMessage;
            }
        });
    }
}
