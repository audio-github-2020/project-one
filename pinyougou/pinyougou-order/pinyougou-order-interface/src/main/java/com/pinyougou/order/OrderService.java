package com.pinyougou.order;

import com.pinyougou.model.Order;
import com.pinyougou.model.PayLog;

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

    /***
     * 修改订单装填和支付日志状态
     * @param username
     * @param transaction_id
     */
    void updatePayStatus(String username, String transaction_id);

    PayLog getPayLogByUserId(String username);

}
