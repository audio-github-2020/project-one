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
public class SetTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testAdd() {
        redisTemplate.boundSetOps("username").add("blue");
        redisTemplate.boundSetOps("username").add("black");
        redisTemplate.boundSetOps("username").add("blue");
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
