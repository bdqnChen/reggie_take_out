package com.bdqn.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bdqn.reggie.pojo.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
