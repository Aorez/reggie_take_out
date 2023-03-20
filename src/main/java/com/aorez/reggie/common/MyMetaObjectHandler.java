package com.aorez.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        metaObject.setValue("createUser", BaseContext.getUserId());
        metaObject.setValue("updateUser", BaseContext.getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

        //doFilter，update，updateFill三个方法是同一个线程
        log.info("thread id " + Thread.currentThread().getId());

        metaObject.setValue("updateTime", LocalDateTime.now());

        log.info("ThreadLocal user id " + BaseContext.getUserId());
        metaObject.setValue("updateUser", BaseContext.getUserId());
    }
}
