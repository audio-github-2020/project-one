package com.springdataredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class ListTest {
    @Autowired
    private RedisTemplate redisTemplate;


    //左压入，从队列左边开始加数据
    @Test
    public void testLeftAdd() {
        //单条增加
        redisTemplate.boundListOps("flower").leftPush("green");
        //批量增加
        redisTemplate.boundListOps("flower").leftPushAll("orange","gray");
    }

    //左压出，不要被名字欺骗，就理解成同为左，那就是一个栈的入栈出栈操作，所以一般左压入，右压出
    @Test
    public void testLetfGet(){
        Object flower = redisTemplate.boundListOps("flower").leftPop();
        System.out.println(flower);
    }

    //右压入
    @Test
    public void testRightAdd() {
        //单条增加
        redisTemplate.boundListOps("flower").rightPush("green");
        //批量增加
        redisTemplate.boundListOps("flower").rightPushAll("orange","gray");

    }
    //右压出，不要被名字欺骗，就理解成同为左，那就是一个栈的入栈出栈操作，所以一般左压入，右压出
    @Test
    public void testRightGet(){
        Object flower = redisTemplate.boundListOps("flower").rightPop();
        System.out.println(flower);
    }

    @Test
    public void testGet() {
        Set members = redisTemplate.boundSetOps("username").members();
        System.out.println(members);
    }

    @Test
    public void testDelete() {
        redisTemplate.boundSetOps("username").remove("black");
    }
}
