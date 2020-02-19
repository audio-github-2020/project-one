package com.pinyougou.test;

import com.pinyougou.solr.SolrUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrImportTest {

    private ApplicationContext act;
    private SolrUtil solrUtil;

    @Before
    public void init() {
        act = new ClassPathXmlApplicationContext("classpath:spring/spring-solr.xml");
        solrUtil = act.getBean(SolrUtil.class);
    }

    /**
     * 数据批量导入
     */
    @Test
    public void batchAdd() {
        solrUtil.batchAdd();
    }

    /**
     *
     */
    //@Test
    public void testGetBySpec(){
        solrUtil.queryByCondition("机身内存","16G");
    }


}
