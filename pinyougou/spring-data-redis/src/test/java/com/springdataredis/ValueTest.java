package com.springdataredis;

import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring-redis-demo.xml")
public class ValueTest {

    //@Autowired
    private RedisTemplate redisTemplate;

    //@Test
    public void testAdd(){
        //boundValueOps用于操作简单的key-value类型
        redisTemplate.boundValueOps("username").set("red");
    }
    //@Test
    public void testGet(){
        Object username = redisTemplate.boundValueOps("username").get();
        System.out.println(username);
    }
    //@Test
    public void testDelete(){
        redisTemplate.delete("username");
    }

}
