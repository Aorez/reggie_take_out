package com.aorez.reggie.service.impl;

import com.aorez.reggie.entity.ShoppingCart;
import com.aorez.reggie.mapper.ShoppingCartMapper;
import com.aorez.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
