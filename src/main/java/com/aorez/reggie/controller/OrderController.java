package com.aorez.reggie.controller;

import com.aorez.reggie.common.R;
import com.aorez.reggie.entity.Orders;
import com.aorez.reggie.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        if (orderService.submit(orders)) {
            return R.success("提交成功");
        }

        return R.error("提交失败");
    }
}
