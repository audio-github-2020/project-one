package com.itheima.mq.queue;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DemoQueueConsumer {

    //接收消息：监听模式
    public static void main(String[] args) throws Exception {
        //创建链接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.12.128:61616");
        //创建链接对象
        Connection connection = connectionFactory.createConnection();

        //开启链接
        connection.start();
        ;

        //创建会话对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //指定接收的队列地址
        Queue queue = session.createQueue("queue_text");

        //创建消息接收对象
        MessageConsumer consumer = session.createConsumer(queue);

        //接收消息：监听模式
        //MessageListener的实现类
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if(message!=null){
                    if(message instanceof TextMessage){
                        TextMessage textMessage= (TextMessage) message;
                        try {
                            System.out.println("监听模式接收到的信息："+textMessage.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //监听模式是创造了一个子线程，一定要让子线程在主线程前结束，换言之就是主线程要等一等子线程
        Thread.sleep(10000);


        //关闭资源
        session.close();
        connection.close();
    }

    //接收消息：Receive方式
    public static void main_Receive(String[] args) throws Exception {
        //创建链接工厂对象
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.12.128:61616");
        //创建链接对象
        Connection connection = connectionFactory.createConnection();

        //开启链接
        connection.start();
        ;

        //创建会话对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        //指定接收的队列地址
        Queue queue = session.createQueue("queue_text");

        //创建消息接收对象
        MessageConsumer consumer = session.createConsumer(queue);

        //接收消息：Receive方式
        while (true) {
            //接收消息
            Message message = consumer.receive(10000);
            if (message != null) {
                if (message instanceof TextMessage) {
                    //将消息强转
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println(textMessage.getText());

                    break;
                }
            }
        }

        //关闭资源
        session.close();
        connection.close();
    }

}
