package com.aorez.reggie.controller;

import com.aorez.reggie.common.BaseContext;
import com.aorez.reggie.common.R;
import com.aorez.reggie.entity.Category;
import com.aorez.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Category category) {

        Long employee = (Long) request.getSession().getAttribute("employee");
        BaseContext.setUserId(employee);

        if (categoryService.save(category)) {
            return R.success("添加成功");
        }

        return R.error("添加失败");
    }

    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        Page<Category> categoryPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        lqw.orderByAsc(Category::getSort);

        categoryService.page(categoryPage, lqw);

        return R.success(categoryPage);
    }

    @DeleteMapping
    public R<String> delete(Long ids) {
        if (categoryService.deleteById(ids)) {
            return R.success("分类信息删除成功");
        }

        return R.error("分类信息删除失败");
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Category category) {

        BaseContext.setUserId((Long) request.getSession().getAttribute("employee"));

        if (categoryService.updateById(category)) {
            return R.success("分类信息修改成功");
        }

        return R.error("分类信息修改失败");
    }

    //新增菜品时查询分类信息
    //参数用category不用type，为了复用
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        lqw.eq(category.getType() != null, Category::getType, category.getType());

        lqw.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> categoryList = categoryService.list(lqw);

        return R.success(categoryList);
    }
}
