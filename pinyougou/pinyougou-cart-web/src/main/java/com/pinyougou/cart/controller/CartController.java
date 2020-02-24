package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.http.Result;
import com.pinyougou.model.Cart;
import com.pinyougou.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    public List<Cart> list() {
        //用户未登录，数据存在cookie中
        String json = CookieUtil.getCookieValue(request, "CookieName", "UTF-8");

        //将购物车数据转成List集合
        List<Cart> carts = JSON.parseArray(json, Cart.class);

        if(carts==null){
            carts=new ArrayList<Cart>();
        }
        //用户已登录

        return carts;
    }

    /**
     * 加入购物车
     *
     * @param itemid
     * @param num
     * @return
     */
    @RequestMapping(value = "/add")
    public Result add(Long itemid, Integer num) {
        //调用查询购物车集合
        List<Cart> carts = list();

        for (Cart cart : carts) {
            System.out.println(cart+"2312");
        }

        //调用加入购物车方法
        cartService.add(carts, itemid, num);

        //List变成JSON，再转到COOKIE
        String json = JSON.toJSONString(carts);

        //将JSON数据存入COOKIE
        CookieUtil.setCookie(request, response, "CookieName", json, 60 * 60 * 24 * 30, "UTF-8");

        return new Result(true, "加入购物车成功");
    }
}
