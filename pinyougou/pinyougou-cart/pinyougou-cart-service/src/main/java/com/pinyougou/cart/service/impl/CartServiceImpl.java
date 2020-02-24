package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Cart;
import com.pinyougou.model.Item;
import com.pinyougou.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public List<Cart> add(List<Cart> carts, Long itemId, Integer num) {
        if(carts==null){
            carts=new ArrayList<Cart>();
        }


        //查询商品信息
        Item item = itemMapper.selectByPrimaryKey(itemId);

        //查询用户已经存在的购物车
        //carts有可能存在当前要添加购物车的商品的商家对应的购物车
        //判断当前要添加购物车的商品的商家对应的购物车是否已经存在
        Cart cart = searchCart(carts, item.getSellerId());

        //如果购物车存在，则获取该商家的购物车信息
        if (cart != null) {
            //获取该商家购物车的所有商品明细，检查当前需要购买的商品，是否已经加入了购物车
            OrderItem orderItem = searchOrderItem(cart.getOrderItemList(), itemId);

            //如果存在该商品购物车明细，则让商品对应的数量增加，并重新计算总价格
            if (orderItem != null) {
                //数量重算
                orderItem.setNum(orderItem.getNum() + num);

                //价格重算
                double totalFee = orderItem.getPrice().doubleValue() * orderItem.getNum();
                orderItem.setTotalFee(new BigDecimal(totalFee));
            } else {
                //如果不存在，则创建新的OrderItem商品明细，添加到商家购物车中
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }
        } else {
            //给商家创建一个cart购物车对象
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            //创建一个OrderItem对象
            OrderItem orderItem = createOrderItem(item, num);
            cart.getOrderItemList().add(orderItem);

            //将新建的Cart加入到carts中
            carts.add(cart);
        }
        return carts;
    }

    /**
     * 创建OrderItem对象
     *
     * @param item
     * @param num
     * @return
     */
    public OrderItem createOrderItem(Item item, Integer num) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setPicPath(item.getImage());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        double totalFee = orderItem.getNum() * orderItem.getPrice().doubleValue();
        orderItem.setTotalFee(new BigDecimal(totalFee));
        return orderItem;
    }

    /**
     * 查询商品明细
     *
     * @param orderItemList
     * @param itemId
     * @return
     */
    public OrderItem searchOrderItem(List<OrderItem> orderItemList, Long itemId) {
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 查询商家的购物车信息
     *
     * @param carts
     * @param sellerId
     * @return
     */
    public Cart searchCart(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }
}
