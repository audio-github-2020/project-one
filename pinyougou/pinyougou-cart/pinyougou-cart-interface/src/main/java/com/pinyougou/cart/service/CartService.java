package com.pinyougou.cart.service;

import com.pinyougou.model.Cart;

import java.util.List;

public interface CartService {

    /**
     * 加入购物车
     *
     * @param carts：已经拥有的购物车机和
     * @param itemId：要加入购物车的item的id
     * @param num：要购买的数量
     * @return
     */
    List<Cart> add(List<Cart> carts, Long itemId, Integer num);

    /**
     * 加入购物车
     *
     * @param username
     * @param carts
     */
    void addGoodsToRedis(String username, List<Cart> carts);

    List<Cart> findCartListFromRedis(String username);

    /**
     * 合并购物侧
     * @param rediscarts
     * @param cookiecarts
     * @return
     */
    List<Cart> mergeList(List<Cart> rediscarts, List<Cart> cookiecarts);
}
