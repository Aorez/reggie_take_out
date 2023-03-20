package com.aorez.reggie.service;

import com.aorez.reggie.dto.DishDto;
import com.aorez.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    public boolean saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public boolean updateWithFlavor(DishDto dishDto);
}
