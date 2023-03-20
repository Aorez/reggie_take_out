package com.aorez.reggie.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//返回结果集
@Data
public class R<T> {
    //编码，1成功
    private Integer code;
    //错误信息
    private String msg;
    //数据
    private T data;
    //动态数据
    private Map map = new HashMap();

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public static <T> R<T> success(T object) {
        R<T> r = new R<>();
        r.code = 1;
        r.data = object;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R<T> r = new R<>();
        r.code = 0;
        r.msg = msg;
        return r;
    }
}
