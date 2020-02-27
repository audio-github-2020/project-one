package com.pinyougou.seckill.service;

import com.pinyougou.model.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    List<SeckillGoods> list();

    SeckillGoods getOne(Long id);
}
