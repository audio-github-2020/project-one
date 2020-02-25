package com.pinyougou.order;

import com.pinyougou.model.Order;

/***
 *
 * @Author:shenkunlin
 * @Description:itheima
 * @date: 2018/9/25 17:55
 *
 ****/
public interface OrderService {

    /***
     * 增加订单信息入库
     * @param order
     * @return
     */
    int add(Order order);

}
