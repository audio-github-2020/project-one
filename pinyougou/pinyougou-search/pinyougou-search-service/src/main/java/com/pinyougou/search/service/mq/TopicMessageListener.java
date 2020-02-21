package com.pinyougou.search.service.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.List;

public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        //解析数据
        if (message instanceof ObjectMessage) {
            //强转
            ObjectMessage objectMessage = (ObjectMessage) message;
            //获取消息内容
            try {
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();

                //如果是修改，则增加内容
                if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {
                    //获取内容
                    List<Item> items = (List<Item>) messageInfo.getContext();
                    //增加索引
                    itemSearchService.importList(items);
                } else if (messageInfo.getMethod() == MessageInfo.METHOD_DELETE) {//如果是删除，则删除内容
                    //获取内容
                    List<Long> ids=(List<Long>)messageInfo.getContext();
                    //增加索引
                    itemSearchService.deleteByGoodsIds(ids);
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }

        }
    }
}
