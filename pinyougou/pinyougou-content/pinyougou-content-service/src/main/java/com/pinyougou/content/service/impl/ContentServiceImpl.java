package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.model.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 返回Content全部列表
     *
     * @return
     */
    @Override
    public List<Content> getAll() {
        return contentMapper.selectAll();
    }

    /***
     * 分页返回Content列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<Content> getAll(Content content, int pageNum, int pageSize) {
        //执行分页
        PageHelper.startPage(pageNum, pageSize);

        //执行查询
        List<Content> all = contentMapper.select(content);
        PageInfo<Content> pageInfo = new PageInfo<Content>(all);
        return pageInfo;
    }

    /***
     * 增加Content信息
     * @param content
     * @return
     */
    @Override
    public int add(Content content) {
        int acount = contentMapper.insertSelective(content);

        //清空缓存
        if (acount > 0) {
            redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }

        return acount;
    }

    /***
     * 根据ID查询Content信息
     * @param id
     * @return
     */
    @Override
    public Content getOneById(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /***
     * 根据ID修改Content信息
     * @param content
     * @return
     */
    @Override
    public int updateContentById(Content content) {
        int acount = contentMapper.updateByPrimaryKeySelective(content);

        //清空缓存
        if (acount > 0) {
            redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
        }

        return acount;
    }

    /***
     * 根据ID批量删除Content信息
     * @param ids
     * @return
     */
    @Override
    public int deleteByIds(List<Long> ids) {
        //先清空缓存，再从数据库删除
        //根据广告id查询广告内容 select * from tb_content where id in(1,2,3)
        Example example1 = new Example(Content.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("id", ids);
        List<Content> contents = contentMapper.selectByExample(example1);

        //创建Example，来构建根据ID删除数据
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        //所需的SQL语句类似 delete from tb_content where id in(1,2,5,6)
        criteria.andIn("id", ids);
        int dcount = contentMapper.deleteByExample(example);

        if (dcount > 0) {
            for (Content content : contents) {
                redisTemplate.boundHashOps("Content").delete(content.getCategoryId());
            }
        }

        return dcount;
    }

    @Override
    public List<Content> findByCategoryId(long categoryId) {

        //第2次及以后查询，先去缓存判断是否有数据
        Object result = redisTemplate.boundHashOps("Content").get(categoryId);
        if (result != null) {
            return (List<Content>) result;
        }

        //根据分类id查询、根据状态筛选、根据排序进行排序
        Example example = new Example(Content.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("categoryId", categoryId);
        criteria.andEqualTo("status", "1");
        example.setOrderByClause("sort_order desc");
        List<Content> contents = contentMapper.selectByExample(example);

        //第一次查询，如果数据不为空，存入redis缓存
        if (contents != null && contents.size() > 0) {
            redisTemplate.boundHashOps("Content").put(categoryId, contents);
        }
        return contents;
    }
}
