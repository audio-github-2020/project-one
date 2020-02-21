package com.pinyougou;

import com.domain.Item;
import org.junit.runner.RunWith;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class SolrTest {

    //@Autowired
    private SolrTemplate solrTemplate;

    /**
     * 删除所有
     */
    //@Test
    public void testDeleteAll(){
        SimpleQuery query = new SimpleQuery("*:*");//相当于把id:1变成都不指定具体值
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 增加一个
     */
    //@Test
    public void testAdd(){
        Item item = new Item();
        item.setId(1L);
        item.setBrand("华为");
        item.setCategory("手机");
        item.setGoodsId(1L);
        item.setSeller("华为2号专卖店");
        item.setTitle("华为Mate9");
        item.setPrice(new BigDecimal(2000));

        //执行保存操作
        solrTemplate.saveBean(item);

        //提交
        solrTemplate.commit();
    }

    /**
     * 批量增加
     */
    //@Test
    public void testBatchAdd(){
        List<Item> items=new ArrayList<>();
        for (int i = 0; i <10 ; i++) {
            Item item = new Item();
            item.setId(10L+(int)(Math.random()*100));
            item.setBrand("华为"+(int)(Math.random()*100));
            item.setCategory("手机"+(int)(Math.random()*100));
            item.setGoodsId(1L);
            item.setSeller("华为2号专卖店"+(int)(Math.random()*100));
            item.setTitle("华为Mate9"+(int)(Math.random()*100));
            item.setPrice(new BigDecimal(20+(int)(Math.random()*100)));
            items.add(item);
        }


        //执行保存操作
        solrTemplate.saveBeans(items);
        //提交
        solrTemplate.commit();
    }


    /**
     * 根据Id删除
     */
    //@Test
    public void testDeleteById(){
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }



    /**
     * 分页查询
     */
    //@Test
    public void testGetPage(){
        //创建Query指定查询条件
        Query query=new SimpleQuery("*:*");

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


    /**
     * 条件查询，同时分页
     */
    //@Test
    public void testQueryByConditionForPage(){
        //创建Query指定查询条件
        Query query=new SimpleQuery("item_brand: 华为86");

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

    /**
     * 条件查询，同时分页
     */
    //@Test
    public void testQueryByConditionForPage_2(){
        //创建Query指定查询条件
        Query query=new SimpleQuery();

        //设置条件
        //分词
         Criteria criteria=new Criteria("item_seller").is("华为专卖店");
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
