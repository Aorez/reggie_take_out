package com.aorez.reggie.controller;

import com.aorez.reggie.common.R;
import com.aorez.reggie.dto.SetmealDto;
import com.aorez.reggie.entity.Setmeal;
import com.aorez.reggie.service.CategoryService;
import com.aorez.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    //添加信息中有菜品信息，用SetmealDto
    //不能用@cache put，因为返回值不是cache所要求的，而且数据是作为list一个整体进行缓存的，无法动态添加到redis中去
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        if (setmealService.saveWithDish(setmealDto)) {
            return R.success("套餐添加成功");
        }

        return R.error("套餐添加失败");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        //要定义Setmeal类型的page，因为要从records中取数据出来进行分类名称的赋值
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        //查询setmeal表
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        setmealService.page(setmealPage, lqw);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        //records需要处理，不复制
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");

        //加入category name
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            setmealDto.setCategoryName(categoryName);

            return setmealDto;
        }).collect(Collectors.toList());

        //设置records
        setmealDtoPage.setRecords(setmealDtoRecords);

        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(Long ids) {
        if (setmealService.deleteWithDish(ids)) {
            return R.success("套餐删除成功");
        }

        return R.error("套餐删除失败");
    }

    //spring expression language spel
    //R没有实现序列化接口，就没办法将数据序列化到redis中
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        //category id，status
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
        lqw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        lqw.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        List<Setmeal> setmealList = setmealService.list(lqw);
        return R.success(setmealList);
    }
}
