package com.pinyougou.search.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.model.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ItemSearchService.class)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 高亮数据查询显示
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        //条件搜索Query
        //Query query=new SimpleQuery("*:*");
        HighlightQuery query = new SimpleHighlightQuery(new SimpleStringCriteria("*:*"));

        //高亮配置
        highlightSettings(query);

        if (searchMap != null) {
            //关键词key是keyword
            String keyword = (String) searchMap.get("keyword");

            //根据关键词查询
            if (StringUtils.isNotBlank(keyword)) {
                //第一个参数是要查询的域
                Criteria criteria = new Criteria("item_keywords").is(keyword);
                query.addCriteria(criteria);
            }

            //分类过滤
            String category = (String) searchMap.get("category");
            if (StringUtils.isNotBlank(category)) {
                //创建Criteria对象，用于填充对应的搜索条件
                Criteria criteria = new Criteria("item_category").is(category);

                //搜索过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery();
                filterQuery.addCriteria(criteria);

                //将搜索过滤对象加入到Query
                query.addFilterQuery(filterQuery);
            }

            //品牌过滤
            String brand = (String) searchMap.get("brand");
            if (StringUtils.isNotBlank(brand)) {
                //创建Criteria对象
                Criteria criteria = new Criteria("item_brand").is(brand);

                //搜索过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);

                //搜索过滤对象加入到Query中
                query.addFilterQuery(filterQuery);
            }
            //接收规格数据
            Object spec = searchMap.get("spec");
            if (spec != null) {
                //$scope.searchMap={"keyword":"","category":"","brand":"",
                // spec:{"网络制式":"联通3G","机身内存":"16G","尺码":"175寸"};
                //过滤搜索规格数据
                Map<String, String> specMap = JSON.parseObject(spec.toString(), Map.class);

                //循环迭代搜索
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    //获取key=网络制式
                    String key = entry.getKey();
                    //要搜索的数据 value = 联通3G
                    String value = entry.getValue();

                    //创建Criteria     key = 网络制式
                    Criteria criteria = new Criteria("item_spec_" + key).is(value);
                    //创建FilterQuery
                    FilterQuery filterQuery = new SimpleFilterQuery(criteria);
                    //添加到Query中
                    query.addFilterQuery(filterQuery);
                }
            }

            //价格区间搜索
            String price = (String) searchMap.get("price");
            if (StringUtils.isNotBlank(brand)) {
                String[] ranges = price.split("-");
                Criteria criteria = new Criteria("item_price");
                //有明确价格区间的
                if (ranges != null && ranges.length == 2) {
                    //创建Criteria对象
                    //  select * from tb_item where price between x and y;
                    criteria.between(Long.parseLong(ranges[0]),
                            Long.parseLong(ranges[1]),
                            true, false);

                }

                //价格3000元以上的
                ranges = price.split(" ");
                if (ranges != null && ranges.length == 2) {
                    //创建Criteria对象
                    //  select * from tb_item where price between x and null;
                    //criteria.between(Long.parseLong(ranges[0]),null,true, false);
                    criteria.greaterThanEqual(Long.parseLong(ranges[0]));

                }
                //搜索过滤对象
                FilterQuery filterQuery = new SimpleFilterQuery(criteria);

                //搜索过滤对象加入到Query中
                query.addFilterQuery(filterQuery);
            }

        }
        //分页
        query.setOffset(0);
        query.setRows(10);

        //执行查询
        //非高亮查询ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
        //返回的结果包含非高亮数据和高亮数据
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);

        //高亮数据替换
        highlightReplace(highlightPage);

        //获取返回结果，封装到Map中
        Map<String, Object> dataMap = new HashMap<>();

        //获取分类分组数据
        List<String> categoryList = getCategory(query);

        //将分类数据存入到Map中，主要作用是页面显示
        dataMap.put("categoryList", categoryList);

//        //默认选中第一个分类,查询分类对应的品牌和规格信息
//        if(categoryList!=null&&categoryList.size()>0){
//            Map<String, Object> specBrandMap = getBrandAndSpec(categoryList.get(0));
//            dataMap.putAll(specBrandMap);
//        }

        //当用户选择了分类的时候，则根据分类检索规格和品牌
        String category = (String) searchMap.get("category");
        if (StringUtils.isNotBlank(category)) {
            dataMap.putAll(getBrandAndSpec(category));
        } else {
            //默认选中第1个分类，查询分类对应的品牌信息和规格信息
            if (categoryList != null && categoryList.size() > 0) {
                //Map<String,Object> specBrandMap =  getBrandAndSpec(categoryList.get(0));
                dataMap.putAll(getBrandAndSpec(categoryList.get(0)));
            }
        }

        //总记录数
        long totalElements = highlightPage.getTotalElements();
        dataMap.put("total", totalElements);

        //结果集
        List<Item> items = highlightPage.getContent();
        dataMap.put("rows", items);

        return dataMap;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取模板ID
     * 同时获取规格和品牌信息
     */
    public Map<String, Object> getBrandAndSpec(String category) {
        Map<String, Object> dataMap = new HashMap<>();

        Long typeTemplateId = (Long) redisTemplate.boundHashOps("ItemCat").get(category);
        if (typeTemplateId != null) {
            //获取品牌信息
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("BrandList").get(typeTemplateId);

            //获取规格信息
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("SpecList").get(typeTemplateId);

            //将品牌和规格存入map
            dataMap.put("brandList", brandList);
            dataMap.put("specList", specList);
        }
        return dataMap;
    }

    /****
     * 高亮数据替换
     * @param highlightPage
     */
    public void highlightReplace(HighlightPage<Item> highlightPage) {
        //高亮信息和非高亮信息的集合
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();

        //循环所有数据
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //获取被循环的非高亮数据
            Item item = itemHighlightEntry.getEntity();

            //获取被循环的高亮数据[假设：它的值只有1条记录]
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();

            //有高亮数据，则替换非高亮数据
            if (highlights != null && highlights.size() > 0) {

                //获取高亮记录
                HighlightEntry.Highlight highlight = highlights.get(0);

                //从高亮记录获取高亮数据[假设：只有1条记录]
                List<String> snipplets = highlight.getSnipplets();

                if (snipplets != null && snipplets.size() > 0) {
                    //获取高亮字符
                    String hlstr = snipplets.get(0);
                    //将非高亮数据替换成高亮数据
                    item.setTitle(hlstr);
                }
            }
        }
    }

    /****
     * 高亮设置
     * @param query
     */
    public void highlightSettings(HighlightQuery query) {
        //高亮信息设置
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置item_title为高亮域
        highlightOptions.addField("item_title");
        //设置前缀    例如：    小红<span style="color:red;">吃饭</span>了吗
        highlightOptions.setSimplePrefix("<span style=\"color:red;\">");
        //设置后缀
        highlightOptions.setSimplePostfix("</span>");

        //两高亮选项添加到Query
        query.setHighlightOptions(highlightOptions);
    }

    /**
     * 获取分类分组数据
     *
     * @param query
     * @return
     */
    public List<String> getCategory(HighlightQuery query) {
        //分组查询，条件封装都使用上面的Query对象
        GroupOptions groupOptions = new GroupOptions();

        //指定对应的分组域
        groupOptions.addGroupByField("item_category");

        //将分组设置添加到Query中
        query.setGroupOptions(groupOptions);

        //执行查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);

        //获取对应域的分组结果
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");

        //groupResult中的数据，具备键值对结构
        List<String> categoryList = new ArrayList<String>();
        for (GroupEntry<Item> itemGroupEntry : groupResult.getGroupEntries()) {
            //获取对应结果集
            String groupValue = itemGroupEntry.getGroupValue();
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    //没有高亮显示关键词的版本
    public Map<String, Object> searchWithOutHigLght(Map<String, Object> searchMap) {
        //条件搜索Query
        Query query = new SimpleQuery("*:*");

        if (searchMap != null) {
            //关键词key是keyword
            String keyword = (String) searchMap.get("keyword");

            //根据关键词查询
            if (StringUtils.isNotBlank(keyword)) {
                //第一个参数是要查询的域
                Criteria criteria = new Criteria("item_keywords").is(keyword);
                query.addCriteria(criteria);
            }

        }
        //分页
        query.setOffset(0);
        query.setRows(10);

        //执行查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);

        //获取返回结果，封装到Map中
        Map<String, Object> dataMap = new HashMap<>();

        //总记录数
        long totalElements = scoredPage.getTotalElements();
        dataMap.put("total", totalElements);

        //结果集
        List<Item> items = scoredPage.getContent();
        dataMap.put("rows", items);

        return dataMap;
    }
}
