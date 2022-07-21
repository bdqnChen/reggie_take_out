package com.bdqn.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bdqn.reggie.common.R;
import com.bdqn.reggie.dto.SetmealDto;
import com.bdqn.reggie.pojo.Category;
import com.bdqn.reggie.pojo.Setmeal;
import com.bdqn.reggie.pojo.SetmealDish;
import com.bdqn.reggie.service.CategoryService;
import com.bdqn.reggie.service.SetmealDishService;
import com.bdqn.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> sava(@RequestBody SetmealDto setmealDto){;

        setmealService.savaWithDish(setmealDto);
        String key = "setmeal_" + setmealDto.getCategoryId() + "_" + setmealDto.getStatus();
        redisTemplate.delete(key);
        return R.success("添加成功");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page , int pageSize , String name){
        //分页查询构造器
        Page<Setmeal> pageinfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDishPage = new Page<>();

        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //创建查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //创建排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageinfo);
        //对象拷贝
        BeanUtils.copyProperties(pageinfo,setmealDishPage,"records");

        List<Setmeal> records = pageinfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //分类ID
            Long categoryId = item.getCategoryId();
            //根据分类ID查询分类对象
            Category category = categoryService.getById(categoryId);
            //把分类对象的套餐名赋给DTO
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDishPage.setRecords(list);

        return R.success(setmealDishPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();
        //先从redis中查询数据
        List<Setmeal> list = null;
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);

        //如果数据存在就直接放回
        if (list != null){
            return R.success(list);
        }

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        list = setmealService.list(queryWrapper);
        redisTemplate.opsForValue().set(key,list,60, TimeUnit.MINUTES);

        return R.success(list);
    }
}