package com.bdqn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.reggie.common.CustomException;
import com.bdqn.reggie.dto.SetmealDto;
import com.bdqn.reggie.mapper.SetmealMapper;
import com.bdqn.reggie.pojo.Setmeal;
import com.bdqn.reggie.pojo.SetmealDish;
import com.bdqn.reggie.service.SetmealDishService;
import com.bdqn.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐，保存套餐信息和套餐关系,两表添加(Setmeal,SetmealDish)
     * @param setmealDto
     */
    @Override
    @Transactional
    public void savaWithDish(SetmealDto setmealDto) {
        //添加套餐基本信息Setmeal
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加套餐关系信息SetmealDish
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，删除套餐信息和套餐关系,两表删除(Setmeal,SetmealDish)
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //不能删除，抛出业务异常
        if (count > 0){
            throw new CustomException("套餐正在售卖不能删除");
        }
        //删除套餐信息
        this.removeByIds(ids);
        //删除套餐关系
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }
}
