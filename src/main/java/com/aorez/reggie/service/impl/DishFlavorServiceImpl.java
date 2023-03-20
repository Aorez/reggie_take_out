package com.aorez.reggie.service.impl;

import com.aorez.reggie.entity.DishFlavor;
import com.aorez.reggie.mapper.DishFlavorMapper;
import com.aorez.reggie.service.DishFlavorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
