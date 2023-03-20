package com.aorez.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

//作用是为controller类进行aop增强
@ControllerAdvice(annotations = {RestController.class, Controller.class})
//没有ResponseBody的时候，方法返回值默认是跳转到某个页面，而不是数据，所以要加ResponseBody
//@RestController不用加@ResponseBody，因为注解中已经包含了
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        if (exception.getMessage().contains("Duplicate entry")) {
            String[] messages = exception.getMessage().split(" ");
            log.error("username already exist");
            return R.error(messages[2] + "已存在");
        }

        return R.error("未知错误");
    }

    //处理自定义异常，携带异常信息
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception) {
        return R.error(exception.getMessage());
    }

    //下载菜品图片一直报文件找不到的异常，因此进行全局捕获
    @ExceptionHandler(IOException.class)
    public void exceptionHandler(IOException exception) {
        log.warn("file not found");
        log.warn(exception.getMessage());
    }
}
