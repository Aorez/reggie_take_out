package com.aorez.reggie.service.impl;

import com.aorez.reggie.dto.SetmealDto;
import com.aorez.reggie.entity.Setmeal;
import com.aorez.reggie.entity.SetmealDish;
import com.aorez.reggie.mapper.SetmealMapper;
import com.aorez.reggie.service.SetmealDishService;
import com.aorez.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public boolean saveWithDish(SetmealDto setmealDto) {
        //保存到setmeal表
        //保存后才有setmeal id
        this.save(setmealDto);

        //保存到setmeal dish表
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        Long setmealId = setmealDto.getId();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        return setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public boolean deleteWithDish(Long id) {
        //setmeal表删除
        this.removeById(id);

        //setmeal dish表删除
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealDish::getSetmealId, id);
        return setmealDishService.remove(lqw);
    }
}
