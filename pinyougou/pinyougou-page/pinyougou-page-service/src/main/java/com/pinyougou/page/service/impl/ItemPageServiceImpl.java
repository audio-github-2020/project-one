package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.GoodsDescMapper;
import com.pinyougou.mapper.GoodsMapper;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.model.Goods;
import com.pinyougou.model.GoodsDesc;
import com.pinyougou.model.Item;
import com.pinyougou.page.service.ItemPageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    //文件生成路径和后缀
    @Value("${HTML_PATH}")
    private String HTML_PATH;
    @Value("${HTML_SUFFIX}")
    private String HTML_SUFFIX;


    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactoryBean;

    @Override
    public Boolean buildHtml(Long goodsId) throws Exception {
        //创建数据模型
        Map<String, Object> dataMap = new HashMap<>();
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        List<Item> items = skuList(goodsId);
        dataMap.put("goods", goods);
        dataMap.put("goodsDesc", goodsDesc);
//        dataMap.put("items", JSON.toJSONString(items));
        dataMap.put("items", items);

        //创建Configuration对象
        Configuration configuration = freeMarkerConfigurationFactoryBean.createConfiguration();

        //创建模板对象
        Template template = configuration.getTemplate("item.ftl");

        //指定文件输出对象
//        Writer writer = new FileWriter(new File(
//                "D:/TrainingforJava/java0204/project-one/pinyougou/pinyougou-page/pinyougou-page-service/src/main/webapp/"
//                        + goodsId + ".html"));


        //准备输出   http://xxx/goodsid.html
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(HTML_PATH+goodsId + HTML_SUFFIX),"UTF-8"));

        //合成输出文件
        template.process(dataMap, writer);

        //关闭资源
        writer.flush();
        writer.close();

        return true;
    }

    /**
     * 根据goodsid查询List<Item>
     */
    public List<Item> skuList(Long goodsId){
        //select * from tb_item where status=1 and goodsid=? order by isdefault desc 降序排列
        //Item必须处于上架状态
        Example example=new Example(Item.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andEqualTo("status","1");
        criteria.andEqualTo("goodsId",goodsId);
        example.orderBy("isDefault").desc();

        return itemMapper.selectByExample(example);
    }

    /**
     * 根据Id删除静态页
     * @param id
     */
    @Override
    public void deleteHtml(Long id) {
        File file = new File(HTML_PATH + id + HTML_SUFFIX);
        if(file.exists()){
            //文件存在则删除
            file.delete();
        }
    }
}
