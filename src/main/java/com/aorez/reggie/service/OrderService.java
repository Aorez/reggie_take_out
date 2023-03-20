package com.aorez.reggie.service;

import com.aorez.reggie.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Orders> {

    public boolean submit(Orders orders);
}
