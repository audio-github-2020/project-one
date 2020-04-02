package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.model.User;
import com.pinyougou.user.service.UserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import javax.jms.JMSException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //@Autowired
    //private JmsTemplate jmsTemplate;

    //@Autowired
    //private Destination destination;

    @Value("${sign_name}")
    private String signName;

    @Value("${template_code}")
    private String templateCode;

    /***
     * 根据手机号判断被注册的个数
     * @param phone
     * @return
     */
    @Override
    public int getPhoneCount(String phone) {
        User user = new User();
        user.setPhone(phone);
        return userMapper.selectCount(user);
    }

    /***
     * 根据用户名判断用户存在的个数
     * @param username
     * @return
     */
    @Override
    public int getUserByUserName(String username) {
        User user = new User();
        user.setUsername(username);
        return userMapper.selectCount(user);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */
    @Override
    public User getUserInfoByUserName(String username) {
        User user = new User();
        user.setUsername(username);
        return userMapper.selectOne(user);
    }

    /***
     * 验证码校验
     * @param phone
     * @param code
     * @return
     */
    @Override
    public Boolean checkCode(String phone, String code) {
        //到Redis中查询验证码
        String rediscode = (String) redisTemplate.boundHashOps("MobileInfo").get(phone);

        //验证码为空
        if (rediscode == null) {
            return false;
        }

        //匹配验证码是否一致
        if (!rediscode.equals(code)) {
            return false;
        }
        return true;
    }

    /***
     * 验证码发送
     * @param phone
     */
    @Override
    public void createCode(String phone) throws Exception {
        //生成验证码
        String code = String.valueOf((int) (Math.random() * 10000));

        //将验证码存入到Redis   命名空间  key:value
        redisTemplate.boundHashOps("MobileInfo").put(phone, code);

        //为了变成JSON数据，需要将code存入到Map中，然后转成JOSN
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("code", code);
        String json = JSON.toJSONString(dataMap);

        //利用Spring-ActiveMQ发送短信
        //sendMessage(phone, json);

        //利用Spring-RabbitMQ发送短信
        //sendMessageBySpringRabbitMQ(phone, json);

        //利用Spring-RabbitMQ发送短信
        sendMessageBySpringBootRabbitMQ(phone, json);

    }

    /**
     * 利用Spring-RabbitMQ完成消息发送
     *
     * @param phone
     * @param json
     * @throws Exception
     */
    public void sendMessageBySpringBootRabbitMQ(String phone, String json) throws Exception {
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/spring-rabbitmq.xml");
        //RabbitMQ模板
        RabbitTemplate template = ctx.getBean(RabbitTemplate.class);

        //发送消息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("type", "1");

        msg.put("signName", signName);
        msg.put("templateCode", templateCode);
        msg.put("mobile", phone);
        msg.put("param", json);//这个地方要放JSON类型数据，所以调用前要转换，这里面放的是验证码

        //简单对列的情况下routingKey即为Queue名
        template.convertAndSend("q_hello1", JSON.toJSONString(msg));

        ctx.destroy(); //容器销毁
    }

    /***
     * 利用SpringBoot-ActiveMQ完成消息发送
     * @param phone
     * @param json
     * @throws JMSException
     */
    /*public void sendMessage(String phone, String json) throws JMSException {
        //将消息发送给ActiveMQ
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //创建MapMessage
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("signName", signName);
                mapMessage.setString("templateCode", templateCode);
                mapMessage.setString("mobile", phone);
                mapMessage.setString("param", json);//这个地方要放JSON类型数据，所以调用前要转换，这里面放的是验证码
                return mapMessage;
            }
        });
    }*/

    /**
     * 利用Spring-RabbitMQ完成消息发送
     *
     * @param phone
     * @param json
     * @throws Exception
     */
    public void sendMessageBySpringRabbitMQ(String phone, String json) throws Exception {
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:spring/spring-rabbitmq.xml");
        //RabbitMQ模板
        RabbitTemplate template = ctx.getBean(RabbitTemplate.class);

        //发送消息
        Map<String, Object> msg = new HashMap<String, Object>();
        msg.put("type", "1");

        msg.put("signName", signName);
        msg.put("templateCode", templateCode);
        msg.put("mobile", phone);
        msg.put("param", json);//这个地方要放JSON类型数据，所以调用前要转换，这里面放的是验证码

        template.convertAndSend("type2", JSON.toJSONString(msg));

        ctx.destroy(); //容器销毁
    }


    /***
     * 增加User信息
     * @param user
     * @return
     */
    @Override
    public int add(User user) {
        //创建时间|修改时间
        Date now = new Date();
        user.setUpdated(now);
        user.setCreated(now);

        //MD5加密
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(pwd);
        int acount = userMapper.insertSelective(user);

        if (acount > 0) {
            //让验证码失效
            redisTemplate.boundHashOps("MobileInfo").delete(user.getPhone());
        }
        return acount;
    }
}
