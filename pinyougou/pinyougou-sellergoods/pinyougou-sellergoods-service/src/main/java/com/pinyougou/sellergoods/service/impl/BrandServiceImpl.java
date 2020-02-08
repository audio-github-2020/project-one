package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.model.Brand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageInfo<Brand> getAll(int page, int size) {
        //分页
        PageHelper.startPage(page,size);
        //集合查询
        List<Brand> brands = brandMapper.selectAll();
        //封装PageInfo
        return new PageInfo<>(brands);

    }

    @Override
    public List<Brand> getAll() {

        return brandMapper.getAll();
    }
}
