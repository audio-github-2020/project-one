package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 根据GoodsId生成静态页
     * @param goodsId  查询 Goods、GoodsDesc、Item
     * @return
     */
    Boolean buildHtml(Long goodsId) throws Exception;

    /**
     * 根据Id删除静态页
     * @param id
     */
    void deleteHtml(Long id);
}
