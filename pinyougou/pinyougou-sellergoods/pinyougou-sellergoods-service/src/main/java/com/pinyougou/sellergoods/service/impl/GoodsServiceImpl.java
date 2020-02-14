package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.model.*;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private ItemMapper itemMapper;

    /**
     * 返回Goods全部列表
     *
     * @return
     */
    @Override
    public List<Goods> getAll() {
        return goodsMapper.selectAll();
    }

    /***
     * 分页返回Goods列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Goods> getAll(Goods goods, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);
        //条件查询实现
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        if (goods != null) {
            //商家sellerID查询
            if (StringUtils.isNotBlank(goods.getSellerId())) {
                criteria.andEqualTo("sellerId", goods.getSellerId());
            }
            //状态查询
            if (StringUtils.isNotBlank(goods.getAuditStatus())) {
                criteria.andEqualTo("auditStatus", goods.getAuditStatus());
            }
            //模糊查询 select * from tb_goods where sellerId=? and auditStatus=? and goodsName like '%小红%'
            if (StringUtils.isNotBlank(goods.getGoodsName())) {
                criteria.andLike("auditStatus", "%" + goods.getGoodsName() + "%");
            }
        }
        //执行查询
        List<Goods> all = goodsMapper.selectByExample(example);
        PageInfo<Goods> pageInfo = new PageInfo<Goods>(all);
        return pageInfo;
    }

    /***
     * 增加Goods信息
     * @param goods
     * @return
     */
    @Override
    public int add(Goods goods) {
        //增加tb_goods
        int acount = goodsMapper.insertSelective(goods);

        //增加tb_goods_desc
        //@GeneratedValue(strategy = GenerationType.IDENTITY)   用于获取主键自增值
        GoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescMapper.insertSelective(goodsDesc);
        addItems(goods);

        return acount;
    }

    //增加商品items
    public void addItems(Goods goods) {
        //category
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id());

        //brand
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());

        //seller
        Seller seller = sellerMapper.selectByPrimaryKey(goods.getSellerId());

        //当前时间
        Date now = new Date();

        //如果启用了规格，则批量增加SKU  item
        if (goods.getIsEnableSpec().equals("1")) {
            //增加SKU
            for (Item item : goods.getItems()) {
                //标题  华为荣耀4 16G  联通3G
                String title = "";

                //获取goods的名称
                title = goods.getGoodsName();

                //规格属性   {"机身内存":"16G","网络":"联通3G"}
                Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                for (Map.Entry<String, String> entry : specMap.entrySet()) {
                    //标题  华为荣耀4 16G  联通3G
                    title += "  " + entry.getValue().toString();
                }
                item.setTitle(title);

                //图片
                // [{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXEWAWuHOAAjlKdWCzvg949.jpg"},
                // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXEuAB_ujAAETwD7A1Is158.jpg"},
                // {"color":"蓝色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXFWANtjTAAFa4hmtWek619.jpg"}]
                //调用抽取方法初始化
                goodsParameterInit(goods, itemCat, brand, seller, now, item);

                //增加到数据库
                itemMapper.insertSelective(item);
            }
        } else {
            Item item = new Item();
            //获取goods的名称
            String goodsName = goods.getGoodsName();
            item.setTitle(goodsName);
            //调用抽取方法初始化
            goodsParameterInit(goods, itemCat, brand, seller, now, item);

            //缺4个
            item.setPrice(goods.getPrice());    //价格
            item.setStatus("1");            //是否启用
            item.setNum(1);                 //数量
            item.setIsDefault("1");         //是否是默认的商品

            //增加到数据库
            itemMapper.insertSelective(item);
        }
    }

    //商品信息初始化
    public void goodsParameterInit(Goods goods, ItemCat itemCat, Brand brand, Seller seller, Date now, Item item) {
        //图片
        // [{"color":"白色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXEWAWuHOAAjlKdWCzvg949.jpg"},
        // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXEuAB_ujAAETwD7A1Is158.jpg"},
        // {"color":"蓝色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmNXFWANtjTAAFa4hmtWek619.jpg"}]
//        String goodsDescitemimages = goods.getGoodsDesc().getItemImages();
//        List<Map> imagesMap = JSON.parseArray(goodsDescitemimages,Map.class);
//        String imageimage = imagesMap.get(0).get("url").toString();
//        item.setImage(imageimage);

        //分类ID
        item.setCategoryid(goods.getCategory3Id());
        //创建时间，修改时间
        item.setUpdateTime(now);
        item.setCreateTime(now);

        //设置SKU也就是goods的id
        item.setGoodsId(goods.getId());

        //设置sellerid
        item.setSellerId(goods.getSellerId());

        //category
        item.setCategory(itemCat.getName());

        //brand
        item.setBrand(brand.getName());

        //seller
        item.setSeller(seller.getName());
    }

    /***
     * 根据ID查询Goods信息
     * @param id
     * @return
     */
    @Override
    public Goods getOneById(Long id) {
        //在修改功能中，需要查询Goods、GoodsDesc、List<Item>
        Goods goods = goodsMapper.selectByPrimaryKey(id);

        GoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(goodsDesc);

        Item item = new Item();
        item.setGoodsId(id);
        List<Item> items = itemMapper.select(item);
        goods.setItems(items);
        return goods;
    }

    /***
     * 根据ID修改Goods信息
     * @param goods
     * @return
     */
    @Override
    public int updateGoodsById(Goods goods) {
        //修改Goods,修改之后，状态应该变为待审核
        goods.setAuditStatus("0");
        int mcount = goodsMapper.updateByPrimaryKeySelective(goods);
        //修改GoodsDesc
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //删除items
        Item item = new Item();
        item.setGoodsId(goods.getId());
        itemMapper.delete(item);
        //新增items
        goods.getItems();
        addItems(goods);
        return goodsMapper.updateByPrimaryKeySelective(goods);
    }

    /***
     * 根据ID批量删除Goods信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //创建Example，来构建根据ID删除数据
        Example example = new Example(Goods.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_goods where id in(1,2,5,6)
        criteria.andIn("id", ids);
        return goodsMapper.deleteByExample(example);
    }
}
