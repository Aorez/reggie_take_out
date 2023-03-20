package com.aorez.reggie.service.impl;

import com.aorez.reggie.entity.OrderDetail;
import com.aorez.reggie.mapper.OrderDetailMapper;
import com.aorez.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
