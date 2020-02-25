package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Order;
import com.pinyougou.model.OrderItem;
import com.pinyougou.order.OrderService;
import com.pinyougou.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/25 17:55
 *
 ****/
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    /***
     * 增加订单入库
     * @param order
     * @return
     */
    @Override
    public int add(Order order) {
        //取出Redis中的购物车数据--购物车明细
        List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps("CartList40").get(order.getUserId());

        //循环购物车数据，根据每个商家的购物车数据创建对应的订单


        for (Cart cart : carts) {
            //创建ID
            Long id = idWorker.nextId();
            //创建新的订单
            Order newOrder = new Order();
            newOrder.setOrderId(id);
            //买家ID
            newOrder.setUserId(order.getUserId());
            //创建时间
            newOrder.setCreateTime(order.getCreateTime());
            //修改时间
            newOrder.setUpdateTime(order.getUpdateTime());
            //状态    1未付款
            newOrder.setStatus(order.getStatus());
            //来源  PC
            newOrder.setSourceType(order.getSourceType());
            //商家ID
            newOrder.setSellerId(cart.getSellerId());

            //设置支付类型
            newOrder.setPaymentType(order.getPaymentType());

            //总金额
            double totalFee = 0;

            //循环增加订单明细
            for (OrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                //设置订单编号
               orderItem.setOrderId(id);

                //设置商家ID
                orderItem.setSellerId(cart.getSellerId());

                //总金额计算
                //Java在java.math包中提供的API类BigDecimal，用来对超过16位有效位的数进行精确的运算。双精度浮点型变量double可以处理16位有效数。
                totalFee+=orderItem.getTotalFee().doubleValue();

                //增加订单明细
                orderItemMapper.insertSelective(orderItem);
            }

            //设置订单总金额
            newOrder.setPayment(new BigDecimal(totalFee));

            //增加订单
            orderMapper.insertSelective(newOrder);
        }

        // XML 文件 forEach 批量增加

        //清空购物车
        redisTemplate.boundHashOps("CartList40").delete(order.getUserId());

        //Cart1         "神州数码"
        //              item:5个
        //              998 8折


        //Cart2         "三星手机"
        //              item:2
        //              1998  无折扣

        return 1;
    }
}
