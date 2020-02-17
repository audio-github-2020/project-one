package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索方法，用Map接收各种值，同时用Map响应各种参数
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,Object> searchMap);
}
