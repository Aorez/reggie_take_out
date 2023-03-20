package com.aorez.reggie.service.impl;

import com.aorez.reggie.entity.User;
import com.aorez.reggie.mapper.UserMapper;
import com.aorez.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
