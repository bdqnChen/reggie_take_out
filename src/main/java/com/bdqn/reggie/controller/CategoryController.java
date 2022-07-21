package com.bdqn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.reggie.common.R;
import com.bdqn.reggie.pojo.Category;
import com.bdqn.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类和套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类和套餐
     * @param category
     * @return
     */
    @PostMapping
    public R<String> sava(@RequestBody Category category){
        log.info("新增分类或套餐，category:{}",category.toString());
        categoryService.save(category);
        return R.success("新增成功");
    }

    /**
     * 套餐分类分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize){
        log.info("分页查询:page:{} , pageSize:{}",page,pageSize);
        //创建分页构造器
        Page<Category> pageinfo = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //查询
        categoryService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    /**
     * 删除套餐或分类
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除id:{},的分类",ids);

//        categoryService.removeById(ids);
        categoryService.remove(ids);

        return R.success("删除成功");
    }

    /**
     * 根据ID来修改
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);

        categoryService.updateById(category);

        return R.success("修改成功");
    }

    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }

}
