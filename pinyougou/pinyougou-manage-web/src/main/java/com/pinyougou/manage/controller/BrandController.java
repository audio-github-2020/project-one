package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.model.Brand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @GetMapping("/listAll")
    public List<Brand> getAll(){
        return brandService.getAll();
    }

    /**
     * 给前端angularJS响应jsom数据
     * @param page
     * @param size
     * @return
     */
    //@RestController的注解中包含了ResponseBody，所以返回的就是Json数据

    @RequestMapping(value="/list",method = RequestMethod.POST)
    public PageInfo<Brand> list(@RequestParam(value="page",required = false,defaultValue = "1")int page,
                                @RequestParam(value="size",required = false,defaultValue = "10")int size){
        //调用Service返回pageinfo
        PageInfo<Brand> pageInfo=brandService.getAll(page,size);

        return  pageInfo;
    }
}
