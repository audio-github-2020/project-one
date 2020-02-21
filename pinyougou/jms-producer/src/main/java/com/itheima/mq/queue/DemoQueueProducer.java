package com.itheima.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DemoQueueProducer {
    public static void main(String[] args) throws Exception {
        //创建链接对象工厂   JMS只是一个规范，activemq是其的实现
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.12.128:61616");

        //创建链接对象
        Connection connection = connectionFactory.createConnection();

        //开启链接对象
        connection.start();

        //创建会话Session
        //第一个参数：是否开启事务（异步通信一般不需要开事务）
        // 第二个参数：应答模式（AUTO_ACKNOWLEDGE:自动应答、CLIENT_ACKNOWLEDGE:客户端应答（手动应答，不会有重复数据）、DUPS_OK_ACKNOWLEDGE:客户端应答（不应答，会有重复数据））
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //创建消息
        TextMessage textMessage = session.createTextMessage();
        textMessage.setText("Hello ActiveMq!"+Math.random());

        //指定消息发送的目标地址
        Queue queue = session.createQueue("queue_text");//Queue就是点对点模式

        //创建消息发送对象
        MessageProducer messageProducer = session.createProducer(queue);

        //消息发送实现
        messageProducer.send(textMessage);

        //资源关闭
        session.close();
        connection.close();
    }
}
