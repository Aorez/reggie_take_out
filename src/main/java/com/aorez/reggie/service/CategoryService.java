package com.aorez.reggie.service;

import com.aorez.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public boolean deleteById(Long id);
}
