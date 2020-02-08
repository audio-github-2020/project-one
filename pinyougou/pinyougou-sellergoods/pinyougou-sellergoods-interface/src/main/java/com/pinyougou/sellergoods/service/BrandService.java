package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.model.Brand;

import java.util.List;

public interface BrandService {
    /***
     * 分页返回列表
     * @param page
     * @param size
     * @return
     */
    PageInfo<Brand> getAll(int page, int size);

    List<Brand> getAll();


}
