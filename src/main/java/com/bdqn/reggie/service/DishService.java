package com.bdqn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.reggie.dto.DishDto;
import com.bdqn.reggie.pojo.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据，同时操作两张表(dish、dish_flavor)
    public void savaWithFlavor(DishDto dishDto);
    //根据ID查询菜品信息和口味信息，同时查询两张表(dish、dish_flavor)
    public DishDto getWithFlavor(Long id);
    //同时更新菜品信息和菜品口味信息,同时修改两站表(dish、dish_flavor)
    public void updateWithFlavor(DishDto dishDto);
    //同时删除菜品信息和菜品口味信息，同时删除两站表(dish、dish_flavor)
    public Boolean removeWithFlavor(Long ids);
}
