package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> list() {

        return redisTemplate.boundHashOps("SeckillGoods").values();
    }

    @Override
    public SeckillGoods getOne(Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(id);
    }
}
