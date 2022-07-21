package com.bdqn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.reggie.common.R;
import com.bdqn.reggie.dto.DishDto;
import com.bdqn.reggie.pojo.Category;
import com.bdqn.reggie.pojo.Dish;
import com.bdqn.reggie.pojo.DishFlavor;
import com.bdqn.reggie.service.CategoryService;
import com.bdqn.reggie.service.DishFlavorService;
import com.bdqn.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> sava(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto.toString());

        dishService.savaWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 分页查询菜品信息
     * @param pageSize
     * @param page
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int pageSize , int page , String name){

        log.info("分页查询:{},{}",page,pageSize);

        //创建分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加模糊查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        //给dishdto的categoryName赋值
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //对象拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> selectById(@PathVariable Long id){
        DishDto dishDto = dishService.getWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("dishDto:{}",dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("=================>ids:{}",ids);

        Boolean aBoolean = dishService.removeWithFlavor(ids);
        if (aBoolean){
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    /**
     * 修改停售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> status0(Long ids){
        log.info("ids:{}",ids);
        Dish dish = new Dish();
        dish.setStatus(0);
        dish.setId(ids);
        boolean b = dishService.updateById(dish);
        if (b){
            return R.success("状态更改成功");
        }
        return R.error("状态更改失败");
    }

    /**
     * 修改停售状态
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> status1(Long ids){
        log.info("ids:{}",ids);
        Dish dish = new Dish();
        dish.setStatus(1);
        dish.setId(ids);
        boolean b = dishService.updateById(dish);
        if (b){
            return R.success("状态更改成功");
        }
        return R.error("状态更改失败");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //创建构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加查询条件
//        queryWrapper.eq(dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //创建构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(dish.getCategoryId() != null , Dish::getCategoryId , dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //对象拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品的ID
            Long id = item.getId();
            //创建条件构造器
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper();
            //添加查询条件
            queryWrapper1.eq(DishFlavor::getDishId,id);
            //执行操作
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
//        dishDtoPage.setRecords(list);
        return R.success(dtoList);
    }
}
