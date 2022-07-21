package com.bdqn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.reggie.common.CustomException;
import com.bdqn.reggie.mapper.CategoryMapper;
import com.bdqn.reggie.pojo.Category;
import com.bdqn.reggie.pojo.Dish;
import com.bdqn.reggie.pojo.Setmeal;
import com.bdqn.reggie.service.CategoryService;
import com.bdqn.reggie.service.DishService;
import com.bdqn.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据ID来删除分类，删除之前先判断当前ID下有无菜品
     * @param id
     */
    @Override
    public void remove(Long id) {
        //构造查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        lambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        //查询当前分类是否关联了菜品,如果关联了菜品就抛出业务异常
        if (dishService.count(dishLambdaQueryWrapper) > 0){
            //关联了菜品就抛出业务异常
            throw new CustomException("当前菜品下已经关联了");
        }
        //查询当前分类是否关联了套餐，如果关联了套餐就抛出业务异常
        if (setmealService.count(lambdaQueryWrapper) > 0){
            //关联了套餐就抛出业务异常
            throw new CustomException("当前套餐下已经关联了");
        }
        //正常删除
        super.removeById(id);
    }
}
