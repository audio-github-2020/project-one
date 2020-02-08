package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.http.Result;
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
                                @RequestParam(value="size",required = false,defaultValue = "10")int size,
                                @RequestBody Brand brand){
        //调用Service返回pageinfo
        PageInfo<Brand> pageInfo=brandService.getAll(page,size,brand);

        return  pageInfo;
    }

    /*
    @RequestMapping(value="/add",method = RequestMethod.POST)
    public Map<String,Object> add(@RequestBody Brand brand){
        //message:消息
        //success:成功/失败状态
        Map<String,Object> result=new HashMap<>();
        try {
            int acount=brandService.add(brand);
            if(acount>0){
                //增加成功
                result.put("success",true);
                result.put("message","增加成功");
                return result;
            }
        } catch (Exception e) {
        }
        //增加失败
        result.put("success",false);
        result.put("message","增加失败");
        return result;
    }*/
    //将map对象直接封装到result对象中
    @RequestMapping(value="/add",method = RequestMethod.POST)
    public Result add(@RequestBody Brand brand){
        //message:消息
        //success:成功/失败状态
        try {
            int acount=brandService.add(brand);
            if(acount>0){
                //增加成功
                return new Result(true,"成功");
            }
        } catch (Exception e) {
        }
        //增加失败
        return new Result(false,"增加失败");
    }

    /****
     * http://localhost:18082/brand/123.shtml
     * http://localhost:18082/brand/{id}.shtml
     * @return
     */
    @RequestMapping(value = "/{id}")
    public Brand getById(@PathVariable(value = "id")Long id){
        //根据ID查询
        return brandService.getById(id);
    }
    /***
     * 修改品牌
     * @return
     */
    @RequestMapping(value = "/modify")
    public Result modify(@RequestBody Brand brand){
        try {
            int account = brandService.updateById(brand);
            if(account>0){
                return  new Result(true,"修改成功");
            }
        } catch (Exception e) {
        }
        return new Result(false,"修改失败");
    }
    /***
     * 执行删除操作
     * @param ids
     * @return
     */
    @RequestMapping(value = "/delete")
    public Result delete(@RequestBody List<Long> ids){
        try {
            //调用Service实现删除
            int dcount = brandService.deleteByIds(ids);

            if(dcount>0){
                return new Result(true,"删除失败");
            }
        } catch (Exception e) {
        }
        return new Result(false, "删除失败");
    }
}
