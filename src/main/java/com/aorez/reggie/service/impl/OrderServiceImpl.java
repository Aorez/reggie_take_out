package com.aorez.reggie.service.impl;

import com.aorez.reggie.common.BaseContext;
import com.aorez.reggie.common.CustomException;
import com.aorez.reggie.entity.*;
import com.aorez.reggie.mapper.OrderMapper;
import com.aorez.reggie.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public boolean submit(Orders orders) {
        //orders表保存订单信息
        //order detail保存订单的若干商品
        //order detail字段和shopping cart类似，order detail相当于保存每次购物车中的商品数据

        //orders有address book id，pay method，remark

        //保存到orders表
        //保存到order detail表

        //生成订单号
        Long orderNumber = IdWorker.getId();
        orders.setNumber(String.valueOf(orderNumber));
        orders.setStatus(2);

        Long userId = BaseContext.getUserId();
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());

        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null) {
            throw new CustomException("收货地址信息为空");
        }
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setAddress((addressBook.getProvinceName() == null?"":addressBook.getProvinceName())
                + (addressBook.getCityName() == null?"":addressBook.getCityName())
                + (addressBook.getDistrictName() == null?"":addressBook.getDistrictName())
                + (addressBook.getDetail() == null?"":addressBook.getDetail()));

        User user = userService.getById(userId);
        orders.setUserName(user.getName());

        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();
        lqw.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqw);
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空");
        }

        //计算总金额
        //可能因为stream所以要用原子性的integer
        AtomicInteger amount = new AtomicInteger(0);

        //在流中计算金额，并且收集order detail数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setOrderId(orderNumber);
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setNumber(item.getNumber());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());

            return orderDetail;
        }).collect(Collectors.toList());

        orders.setAmount(new BigDecimal(amount.get()));

        this.save(orders);

        orderDetailService.saveBatch(orderDetailList);

        return true;
    }
}
