package com.bdqn.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bdqn.reggie.mapper.DishFlavorMapper;
import com.bdqn.reggie.pojo.DishFlavor;
import com.bdqn.reggie.service.DishFlavorService;
import com.bdqn.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
