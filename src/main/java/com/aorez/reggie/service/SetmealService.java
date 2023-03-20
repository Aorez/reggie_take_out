package com.aorez.reggie.service;

import com.aorez.reggie.dto.SetmealDto;
import com.aorez.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SetmealService extends IService<Setmeal> {

    public boolean saveWithDish(SetmealDto setmealDto);

    public boolean deleteWithDish(Long id);
}
