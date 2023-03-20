package com.aorez.reggie.service.impl;

import com.aorez.reggie.entity.Employee;
import com.aorez.reggie.mapper.EmployeeMapper;
import com.aorez.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
