package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.http.Result;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/seckill/order")
public class SeckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping(value = "/add")
    public Result add(Long id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            if(username.equals("anonymousUser")){
                return new Result(false,"403");
            }

            seckillOrderService.add(username,id);
            return new Result(true,"下单成功");
        } catch (Exception e) {
            return new Result(false,e.getMessage());
        }
    }
}
