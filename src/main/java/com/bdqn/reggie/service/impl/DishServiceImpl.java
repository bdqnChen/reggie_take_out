package com.bdqn.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.reggie.dto.DishDto;
import com.bdqn.reggie.mapper.DishMapper;
import com.bdqn.reggie.pojo.Dish;
import com.bdqn.reggie.pojo.DishFlavor;
import com.bdqn.reggie.service.DishFlavorService;
import com.bdqn.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时保存2张表的数据,同时操作两张表(dish、dish_flavor)
     * @param dishDto
     */
    @Override
    @Transactional
    public void savaWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        //获取菜品ID
        Long dishId = dishDto.getId();

        //
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ID查询菜品信息和菜品口味，同时查询两张表(dish、dish_flavor)
     * @param id
     * @return
     */
    @Override
    public DishDto getWithFlavor(Long id) {
        //查询菜品基本信息dish
        Dish dish = this.getById(id);
        //数据拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品口味信息dish_flavor
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        //添加查询条件
        LambdaQueryWrapper<DishFlavor> eq = queryWrapper.eq(DishFlavor::getDishId, id);
        //执行查询
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        //将菜品口味信息dish_flavor放回给disDto
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 同时更新菜品信息和菜品口味信息,同时修改两站表(dish、dish_flavor)
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品基本信息dish
        this.updateById(dishDto);
        //清楚菜品口味信息数据dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加菜品口味信息dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 同时删除菜品信息和菜品口味信息，同时删除两站表(dish、dish_flavor)
     * @param ids
     */
    @Override
    @Transactional
    public Boolean removeWithFlavor(Long ids) {
        //删除菜品信息
        this.removeById(ids);

        //删除菜品口味信息
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(DishFlavor::getDishId,ids);
        //执行删除
        return dishFlavorService.remove(queryWrapper);
    }
}
