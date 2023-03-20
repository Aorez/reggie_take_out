package com.aorez.reggie.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String number;
    //订单状态，1待付款，2待派送，3已派送，4已完成，5已取消
    private Integer status;
    private Long userId;
    private Long addressBookId;
    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;
    //支付方式，1微信，2支付宝
    private Integer payMethod;
    private BigDecimal amount;
    private String remark;
    private String userName;
    private String phone;
    private String address;
    private String consignee;
}
