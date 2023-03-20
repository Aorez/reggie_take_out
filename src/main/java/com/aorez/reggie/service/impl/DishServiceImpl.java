package com.aorez.reggie.service.impl;

import com.aorez.reggie.dto.DishDto;
import com.aorez.reggie.entity.Dish;
import com.aorez.reggie.entity.DishFlavor;
import com.aorez.reggie.mapper.DishMapper;
import com.aorez.reggie.service.DishFlavorService;
import com.aorez.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Override
    @Transactional
    public boolean saveWithFlavor(DishDto dishDto) {
        //先添加菜品
        this.save(dishDto);

        //再添加菜品口味
        //一个菜品对应诸多口味
        //一个口味对应诸多程度
        List<DishFlavor> dishFlavors = dishDto.getFlavors();

        if (dishFlavors.size() <= 0) {
            return true;
        }

        Long dishId = dishDto.getId();
        //这里不能直接用DishFlavor::setDishId，提示返回值不能为void

        dishFlavors = dishFlavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //idea提示可以替换为peek
        //map和peek接收的参数不一样，map接收一个函数接口，是有返回值的，peek接收一个消费者函数
        //流中的元素通过管道时会进行peek消费
//        dishFlavors = dishFlavors.stream().peek((item) -> item.setDishId(dishId)).collect(Collectors.toList());

        return dishFlavorService.saveBatch(dishFlavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询dish表
        //Dish dish = new Dish();
        //DishDto dishDto = (DishDto) dish;
        //这种写法是错误的，抛出class cast exception
//        DishDto dishDto = (DishDto) this.getById(id);
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询flavor表补充flavor字段
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw);
        dishDto.setFlavors(dishFlavorList);

        log.info("dishDto id " + dishDto.getId());

        return dishDto;
    }

    @Override
    @Transactional
    public boolean updateWithFlavor(DishDto dishDto) {
        //更新菜品表
        this.updateById(dishDto);

        //删除菜品口味表相关信息
        //不能用removeById，因为这个id是dish id
//        dishFlavorService.removeById(dishDto.getId());
        Long dishId = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(DishFlavor::getDishId, dishId);
        dishFlavorService.remove(lqw);

        //更新菜品口味表
        //flavors需要dish id
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

        return true;
    }
}
