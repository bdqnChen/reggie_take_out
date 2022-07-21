package com.bdqn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.reggie.dto.SetmealDto;
import com.bdqn.reggie.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //添加套餐，保存套餐信息和套餐关系,两表添加(Setmeal,SetmealDish)
    public void savaWithDish(SetmealDto setmealDto);
    //删除套餐，删除套餐信息和套餐关系,两表删除(Setmeal,SetmealDish)
    public void removeWithDish(List<Long> ids);
}
