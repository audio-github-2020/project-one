import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public class ContentTest {

    private ApplicationContext act;
    private ContentMapper contentMapper;

    private RedisTemplate redisTemplate;

    //@Before
    public void init() {
        //初始化Spring容器
        act = new ClassPathXmlApplicationContext("spring/spring.xml");
        //从容器中获取BrandMapper的实例
        contentMapper = act.getBean(ContentMapper.class);
        redisTemplate=act.getBean(RedisTemplate.class);
    }

    //@Test
    public void testSpring() {

        //分页
//        PageHelper.startPage(1, 5);

        List<Content> contents = contentMapper.selectAll();

        for (Content content : contents) {
            System.out.println(content);
        }

        //分页信息
//        PageInfo<Content> pageInfo = new PageInfo<Content>(contents);
//        System.out.println(pageInfo);

    }
    //@Test
    public void findByCategoryId_bk() {
        long categoryId=1l;
        //第2次及以后查询，先去缓存判断是否有数据
        Object result= redisTemplate.boundHashOps("Content").get(categoryId);
        if(result!=null){
            System.out.println("111111111111");
            System.out.println("111111111111");
            System.out.println( (List<Content>) result);
            System.out.println("222222222222");
            System.out.println("222222222222");
        }


        //根据分类id查询、根据状态筛选、根据排序进行排序
        Example example=new Example(Content.class);
        Example.Criteria criteria=example.createCriteria();

        criteria.andEqualTo("categoryId",categoryId);
        criteria.andEqualTo("status","1");
        example.setOrderByClause("sort_order desc");
        List<Content> contents = contentMapper.selectByExample(example);

        //第一次查询，如果数据不为空，存入redis缓存
        if(contents!=null&&contents.size()>0){
            System.out.println("3434343434");
            redisTemplate.boundHashOps("Content").put(categoryId,contents);
        }
        System.out.println("33333333333");
        System.out.println("33333333333");
        System.out.println(contents);
        System.out.println("44444444444");
        System.out.println("44444444444");
    }
}
