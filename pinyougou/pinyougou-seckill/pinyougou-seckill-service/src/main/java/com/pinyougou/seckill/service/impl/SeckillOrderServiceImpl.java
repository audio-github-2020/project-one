package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.model.SeckillGoods;
import com.pinyougou.model.SeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.util.IdWorker;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private Redisson redisson;

    @Override
    public void add(String username, Long id) throws InterruptedException {

        //判断用户是否已经存在订单未支付
        Object seckillOrder = redisTemplate.boundHashOps("SeckillOrder").get(username);
        if(seckillOrder!=null){
            throw new RuntimeException("存在未支付订单！");
        }


        RLock lock = redisson.getLock("lockKey");
        try {
            lock.tryLock(30, TimeUnit.SECONDS);
            //从队列中获取一个商品ID
            Object goodid = redisTemplate.boundListOps("SeckillGoods_Id_" + id).rightPop();
            if (goodid == null) {
                throw new RuntimeException("已售罄");
            }
            //根据id获取商品信息
            SeckillGoods good = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(id);
            System.out.println("用户：" + username + ",检测有" + good.getStockCount());

            //根据商品信息创建订单
            SeckillOrder order = new SeckillOrder();
            order.setId(idWorker.nextId());
            order.setSeckillId(id);
            order.setMoney(good.getCostPrice());
            order.setUserId(username);
            order.setSellerId(good.getSellerId());
            order.setCreateTime(new Date());
            order.setStatus("0");

            //将订单存入redis中
            redisTemplate.boundHashOps("SeckillOrder").put(username, order);

            //库存削减，一旦商品售罄，则需要将数据同步到数据库中，并且移除redis中的记录
            good.setStockCount(good.getStockCount() - 1);

            //如果没有售罄，修改redis中的数据即可
            if (good.getStockCount() <= 0) {
                //将数据同步到数据库
                seckillGoodsMapper.updateByPrimaryKeySelective(good);
                //移除redis中的记录
                redisTemplate.boundHashOps("SeckillGoods").delete(id);
            } else {
                redisTemplate.boundHashOps("SeckillGoods").put(id, good);
            }
        } finally {
            lock.unlock();
        }

    }

    /***
     * 根据用户名查询订单信息
     * @param username
     * @return
     */
    @Override
    public SeckillOrder getOrderByUserName(String username) {
        return (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);
    }

    /***
     * 修改订单状态
     * @param username
     * @param transaction_id
     */
    @Override
    public void updatePayStatus(String username, String transaction_id) {
        //SeckillOrder--->从Redis中取出---加入到MySQL
        SeckillOrder order = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        if (order != null) {
            //修改对应的数据
            order.setStatus("1");
            order.setTransactionId(transaction_id);
            order.setPayTime(new Date());

            //持久化到MySQL
            seckillOrderMapper.insertSelective(order);

            //清空Redis中的订单
            redisTemplate.boundHashOps("SeckillOrder").delete(username);
        }
    }

    /***
     * 根据用户名移除订单
     * @param username
     */
    @Override
    public void removeOrder(String username) {
        //获取订单
        SeckillOrder order = (SeckillOrder) redisTemplate.boundHashOps("SeckillOrder").get(username);

        if (order != null) {
            //订单移除
            redisTemplate.boundHashOps("SeckillOrder").delete(username);

            //数据要回滚
            SeckillGoods good = (SeckillGoods) redisTemplate.boundHashOps("SeckillGoods").get(order.getSeckillId());
            if (good == null) {
                //从数据库中加载
                good = seckillGoodsMapper.selectByPrimaryKey(order.getSeckillId());
            }

            //修改库存
            good.setStockCount(good.getStockCount() + 1);

            //存入Redis
            redisTemplate.boundHashOps("SeckillGoods").put(good.getId(), good);
        }
    }
}
