package com.aorez.reggie.service.impl;

import com.aorez.reggie.common.CustomException;
import com.aorez.reggie.entity.Category;
import com.aorez.reggie.entity.Dish;
import com.aorez.reggie.entity.Setmeal;
import com.aorez.reggie.mapper.CategoryMapper;
import com.aorez.reggie.service.CategoryService;
import com.aorez.reggie.service.DishService;
import com.aorez.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    public boolean deleteById(Long id) {
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(lqw1);
        if (count1 > 0) {
            throw new CustomException("该分类与某个菜品有关联，不能删除");
        }

        LambdaQueryWrapper<Setmeal> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(lqw2);
        if (count2 > 0) {
            throw new CustomException("该分类与某个套餐有关联，不能删除");
        }

        return super.removeById(id);
    }
}
