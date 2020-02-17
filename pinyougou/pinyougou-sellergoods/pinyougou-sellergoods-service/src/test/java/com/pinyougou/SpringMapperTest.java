package com.pinyougou;

import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.model.Brand;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

public class SpringMapperTest {

    private ApplicationContext act;
    private BrandMapper brandMapper;

    //@Before
    public void init() {
        //初始化Spring容器
        act = new ClassPathXmlApplicationContext("spring/spring.xml");
        //从容器中获取BrandMapper的实例
        brandMapper = act.getBean(BrandMapper.class);
    }

    //查询所有bean
    //@Test
    public void testbk() {
        String s = "1";
        boolean f = s.equals("1");
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);
        System.out.println(f);

    }

    //查询所有bean
    //@Test
    public void testSpring() {

        //分页
//        PageHelper.startPage(1, 5);

        List<Brand> brands = brandMapper.selectAll();

        for (Brand brand : brands) {
            System.out.println(brand);
        }

        //分页信息
//        PageInfo<Brand> pageInfo = new PageInfo<Brand>(brands);
//        System.out.println(pageInfo);

    }

    //方法后面包含Selective的就是忽略空值null，即未设定某一值的话，该对象的增删改查无法完成

    //增
    //@Test
    public void testInsert() {
        //原始数据
        Brand brand = new Brand();
        brand.setName("redMi");
        //调用增加方法
        brandMapper.insert(brand);
    }

    //删
    //@Test
    public void testDelete() {
        //原始数据
        Brand brand = new Brand();
        brand.setId(24L);
        //调用增加方法
        brandMapper.delete(brand);
    }

    //批量删除
    //@Test
    public void testDeleteByExample() {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //组装条件
        List<Long> ids = new ArrayList<>();
        ids.add(22L);
        ids.add(21L);
        criteria.andIn("id", ids);

        //删除操作
        brandMapper.deleteByExample(example);
    }

    //改
    //@Test
    public void testUpdateByPrimaryKey() {
        //原始数据
        Brand brand = new Brand();
        brand.setId(20l);
        brand.setName("红米");
        brand.setFirstChar("H");
        //调用增加方法
        brandMapper.updateByPrimaryKey(brand);
    }

    //批量修改
    //@Test
    public void testUpdateByExample() {
        //firstChar=S
        Brand brand = new Brand();
        brand.setName("深圳市黑马训练营");
        //创建Example对象
        Example example = new Example(Brand.class);

        //Criteria 用来构造约束条件
        Example.Criteria criteria = example.createCriteria();

        //第一个参数是Brand对应的属性，第二个参数是属性约束值   相当于 where firstChar=S
        criteria.andEqualTo("firstChar", "S");

        //条件修改数据
        int mcount = brandMapper.updateByExample(brand, example);
        System.out.println(mcount);

    }

    //查
    //Test
    public void testSelectByPrimaryKey() {
        long id = 18L;
        Brand brand = brandMapper.selectByPrimaryKey(id);
        System.out.println(brand);
    }

}
