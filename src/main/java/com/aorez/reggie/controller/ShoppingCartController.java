package com.aorez.reggie.controller;

import com.aorez.reggie.common.BaseContext;
import com.aorez.reggie.common.R;
import com.aorez.reggie.entity.ShoppingCart;
import com.aorez.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    //前端需要shopping cart数据，返回值不能是string
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //加入的有可能是菜品或套餐
        //防止数据中dish id和setmeal id同时存在，分开代码
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        //查询shopping cart表，是否已经有该商品数据，有则number加1，否则新建
        if (dishId != null) {
            lqw.eq(ShoppingCart::getDishId, dishId);
        }
        else if (setmealId != null) {
            lqw.eq(ShoppingCart::getSetmealId, setmealId);
        }
        else return R.error("添加失败");

        //需要user id
        //用base context，不用session
        Long userId = BaseContext.getUserId();
        lqw.eq(ShoppingCart::getUserId, userId);

        //数据库中应只有一条数据或没有数据
        ShoppingCart cart = shoppingCartService.getOne(lqw);
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
        }
        else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCart.setUserId(userId);

            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }

        return R.success(cart);
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        Long userId = BaseContext.getUserId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        lqw.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);

        return R.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public R<String> clean() {
        Long userId = BaseContext.getUserId();
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);

        if (shoppingCartService.remove(lqw)) {
            return R.success("清空成功");
        }
        return R.error("清空失败");
    }
}
