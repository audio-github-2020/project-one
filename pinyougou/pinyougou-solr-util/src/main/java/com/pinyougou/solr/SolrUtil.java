package com.pinyougou.solr;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 商品数据批量导入索引库
     *
     */
    public void batchAdd(){
        //查询数据，商品状态必须为上架状态
        //select * from tb_item where status=1
        Item item =new Item();
        item.setStatus("1");
        List<Item> items = itemMapper.select(item);


        //循环，将JSON字符转Map
        for (Item itemMap : items) {
            //规格字符串值
            String specString=itemMap.getSpec();

            //将规格字符串转成Map
            Map<String,String>dataMap= JSON.parseObject(specString,Map.class);
            itemMap.setSpecMap(dataMap);

        }
        //将数据导入索引库
        solrTemplate.saveBeans(items);

        solrTemplate.commit();
    }

    /**
     * 动态域搜索
     */
    public void queryByCondition(String fieldName,String keywords){
        //创建Query指定查询条件
        Query query=new SimpleQuery();

        //设置条件
        //分词
        Criteria criteria=new Criteria("item_spec_"+fieldName).is(keywords);
        //不会分词
        //Criteria criteria=new Criteria("item_seller").contains("华为专卖店");

        //增加条件
        query.addCriteria(criteria);

        //指定分页参数
        query.setOffset(0);
        query.setRows(5);

        //执行分页搜索
        //Query：搜索条件的封装
        //Item.class：查询的数据结果集需要转换成Item
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取结果集
        List<Item> items = scoredPage.getContent();
        //获取总记录数
        long totalElements = scoredPage.getTotalElements();

        System.out.println(totalElements);
        for (Item item : items) {
            System.out.println(item);
        }
    }
}
