package com.tencent.weixin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/pay")
public class PayController {

    /**
     * https://api.mch.weixin.qq.com/pay/unifiedorder
     * 生成支付连接地址
     */

    @RequestMapping(value = "/unifiedorder")
    public Map createNative() {
        return null;
    }

}
