package com.pinyougou.pay.service;

import java.util.Map;

public interface WeixinPayService {

    /**
     * 获取支付二维码的url
     */
    Map<String,String> createNative(String out_trade_no,String total_fee);

    /**
     * 查询支付状态
     */
    Map<String,String> queryPayStatus(String out_trade_no);

    /**
     * 关闭订单
     * @param tradeoutno
     * @return
     */
    Map<String, String> closePay(String tradeoutno);
}
