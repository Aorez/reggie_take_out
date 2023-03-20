package com.aorez.reggie.common;

//ThreadLocal在同一个线程中共用user id，在自动填充中才能获取
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new InheritableThreadLocal<>();

    public static void setUserId(Long id) {
        threadLocal.set(id);
    }

    public static Long getUserId() {
        return threadLocal.get();
    }
}
