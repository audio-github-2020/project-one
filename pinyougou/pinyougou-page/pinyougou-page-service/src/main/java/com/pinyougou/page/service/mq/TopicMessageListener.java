package com.pinyougou.page.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        //消息类型判断
        if (message instanceof ObjectMessage) {
            //强转
            ObjectMessage objectMessage = (ObjectMessage) message;

            //获取内容
            try {
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //判断操作类型
                if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {
                    List<Item> items = (List<Item>) messageInfo.getContext();

                    //获取所有GoodsId，并去除重复
                    Set<Long> ids = getGoodsIds(items);

                    //循环创建静态页
                    for (Long id : ids) {
                        itemPageService.buildHtml(id);
                    }
                } else if (messageInfo.getMethod() == MessageInfo.METHOD_DELETE) {
                    //获取商品id，循环删除
                    List<Long> ids = (List<Long>) messageInfo.getContext();

                    //删除操作
                    for (Long id : ids) {
                        itemPageService.deleteHtml(id);
                    }

                    //获取ID，删除静态页
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<Long> getGoodsIds(List<Item> items) {
        Set<Long> ids = new HashSet<>();
        for (Item item : items) {
            ids.add(item.getGoodsId());
        }
        return ids;
    }
}
