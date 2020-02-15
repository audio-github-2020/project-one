import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

public class ContentTest {

    private ApplicationContext act;
    private ContentMapper contentMapper;

    //@Before
    public void init() {
        //初始化Spring容器
        act = new ClassPathXmlApplicationContext("spring/applicationContext-service.xml");
        //从容器中获取BrandMapper的实例
        contentMapper = act.getBean(ContentMapper.class);
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

}
