package com.aorez.reggie.controller;

import com.aorez.reggie.common.R;
import com.aorez.reggie.dto.DishDto;
import com.aorez.reggie.entity.Category;
import com.aorez.reggie.entity.Dish;
import com.aorez.reggie.entity.DishFlavor;
import com.aorez.reggie.service.CategoryService;
import com.aorez.reggie.service.DishFlavorService;
import com.aorez.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        if (dishService.saveWithFlavor(dishDto)) {
            return R.success("菜品添加成功");
        }

        return R.error("菜品添加失败");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishService.page(dishPage, lqw);

        //page数据records中没有分类名称，只有分类id
        //先将页面信息copy出来
        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //为每个菜品设置分类名称
        //定义一个全新的records
        List<Dish> dishPageRecords = dishPage.getRecords();
        List<DishDto> dishDtoList = dishPageRecords.stream().map((item) -> {
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            dishDto.setCategoryName(categoryName);

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);

        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        if (dishService.updateWithFlavor(dishDto)) {
            return R.success("菜品信息更新成功");
        }

        return R.error("菜品信息更新失败");
    }

//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(Dish::getCategoryId, dish.getCategoryId());
//        List<Dish> dishList = dishService.list(lqw);
//        return R.success(dishList);
//    }
    //移动端需要list展示菜品口味数据
    //请求参数用Dish即可
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        //查询dish表
        //category id，status
        //lqw泛型不能是DishDto，因为dish service与dish绑定了
        LambdaQueryWrapper<Dish> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        lqw1.eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus());
        List<Dish> dishList = dishService.list(lqw1);

        //查询flavor表，set flavor数据
        List<DishDto> dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lqw2 = new LambdaQueryWrapper<>();
            lqw2.eq(dishId != null, DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lqw2);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
