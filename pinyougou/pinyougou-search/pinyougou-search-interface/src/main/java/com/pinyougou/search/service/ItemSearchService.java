package com.pinyougou.search.service;

import com.pinyougou.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索方法，用Map接收各种值，同时用Map响应各种参数
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,Object> searchMap);

    /**
     * 批量导入索引库
     * @param items
     */
    void importList(List<Item> items);

    /**
     * 批量删除索引库数据
     * @param ids
     */
    void deleteByGoodsIds(List<Long> ids);
}
