package com.aorez.reggie.controller;

import com.aorez.reggie.common.BaseContext;
import com.aorez.reggie.common.R;
import com.aorez.reggie.entity.Employee;
import com.aorez.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //转md5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //条件查询
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername, employee.getUsername());
        Employee e = employeeService.getOne(lqw);

        //用户名是否存在
        if (e == null) {
            return R.error("用户不存在");
        }

        if (!e.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        if (e.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //存入session
        request.getSession().setAttribute("employee", e.getId());

        return R.success(e);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除session
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        //公共字段自动填充
        BaseContext.setUserId((Long) request.getSession().getAttribute("employee"));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        Long createUserId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(createUserId);
//        employee.setUpdateUser(createUserId);

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        if (employeeService.save(employee)) {
            log.info("save success");
            return R.success("添加成功");
        }

        //如果添加失败，MySQL会抛出异常，代码不会执行到这里，而是在异常处理器中进行处理
        log.error("save error");
        return R.error("添加失败");
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        Page<Employee> employeePage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        employeeService.page(employeePage, lqw);

        return R.success(employeePage);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {

        //doFilter，update，updateFill三个方法是同一个线程
        log.info("thread id " + Thread.currentThread().getId());

        //ThreadLocal在同一个线程中共用user id，在自动填充中才能获取
        Long userId = (Long) request.getSession().getAttribute("employee");
        BaseContext.setUserId(userId);

//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

//        employee.setUpdateTime(LocalDateTime.now());

        if (employeeService.updateById(employee)) {
            return R.success("修改成功");
        }

        return R.error("修改失败");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee == null) {
            return R.error("员工不存在");
        }
        return R.success(employee);
    }
}
