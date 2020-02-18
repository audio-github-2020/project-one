package com.pinyougou.search.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.model.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ItemSearchService.class)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //条件搜索Query
        Query query=new SimpleQuery("*:*");

        if(searchMap!=null){
            //关键词key是keyword
            String keyword =(String) searchMap.get("keyword");

            //根据关键词查询
            if(StringUtils.isNotBlank(keyword)){
                //第一个参数是要查询的域
                Criteria criteria=new Criteria("item_keywords").is(keyword);
                query.addCriteria(criteria);
            }

        }
        //分页
        query.setOffset(0);
        query.setRows(10);

        //执行查询
        ScoredPage<Item> scoredPage=solrTemplate.queryForPage(query,Item.class);

        //获取返回结果，封装到Map中
        Map<String,Object> dataMap=new HashMap<>();

        //总记录数
        long totalElements = scoredPage.getTotalElements();
        dataMap.put("total",totalElements);

        //结果集
        List<Item> items = scoredPage.getContent();
        dataMap.put("rows",items);

        return dataMap;
    }
}
