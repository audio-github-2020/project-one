package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.http.Result;
import com.pinyougou.model.Cart;
import com.pinyougou.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value = "/list")
    public List<Cart> list() {

        //获取登录账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //用户未登录，数据存在cookie中
        String json = CookieUtil.getCookieValue(request, "CookieName", "UTF-8");

        //将购物车数据转成List集合
        List<Cart> cookiecarts = JSON.parseArray(json, Cart.class);

        if (username.equals("anonymousUser")) {
            return cookiecarts;
        } else {
            //用户已登录
            List<Cart> rediscarts = cartService.findCartListFromRedis(username);

            //合并cookiecarts和rediscarts
            if (cookiecarts != null && cookiecarts.size() != 0) {
                //cookiecarts是登陆前的购物车，rediscarts是登录后的购物车
                rediscarts = cartService.mergeList(rediscarts, cookiecarts);

                //将新集合重新加入redis中，覆盖原来的购物车数据
                cartService.addGoodsToRedis(username, rediscarts);

                //清除原有的cookie数据
                CookieUtil.deleteCookie(request, response, "CookieName");
            }
            return rediscarts;
        }
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
        //获取匿名账号
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //调用查询购物车集合
        List<Cart> carts = list();

        //调用加入购物车方法
        carts = cartService.add(carts, itemid, num);

        if (username.equals("anonymousUser")) {
            //用户未登录
            //List变成JSON，再转到COOKIE
            String json = JSON.toJSONString(carts);

            //将JSON数据存入COOKIE
            CookieUtil.setCookie(request, response, "CookieName", json, 60 * 60 * 24 * 30, "UTF-8");
        } else {
            //用户已登录
            cartService.addGoodsToRedis(username, carts);
        }

        return new Result(true, "加入购物车成功");
    }
}
