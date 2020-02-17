package com.springdataredis;

import org.junit.runner.RunWith;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-redis.xml")
public class HashTest {
    //@Autowired
    private RedisTemplate redisTemplate;


    //Hash类型什么都可以存，但是有两层Key，第一层是namespace，第二层是key
    //@Test
    public void testAdd() {
        redisTemplate.boundHashOps("NameSpace").put("1","building");
        redisTemplate.boundHashOps("NameSpace").put("2","builds");
        redisTemplate.boundHashOps("NameSpace").put("3","built");

        redisTemplate.boundHashOps("NameSpaceBK").put("1","building");
        redisTemplate.boundHashOps("NameSpaceBK").put("3","builds");
        redisTemplate.boundHashOps("NameSpaceBK").put("2","built");
    }

    //查询操作
    //@Test
    public void testGet(){
        Object nameSpace = redisTemplate.boundHashOps("NameSpace").get("3");
        System.out.println(nameSpace);

        //查询所有
        List values = redisTemplate.boundHashOps("NameSpace").values();
        for (Object value : values) {
            System.out.println(value);
        }
    }

    //删除操作
    //@Test
    public void testDelte(){
        redisTemplate.boundHashOps("NameSpace").delete("2");
    }


}
