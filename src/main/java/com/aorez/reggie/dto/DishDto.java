package com.aorez.reggie.dto;

import com.aorez.reggie.entity.Dish;
import com.aorez.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.List;

//继承后可以通过get和set操作父类的私有属性
//继承了父类的序列化
//dto是为了扩展属性
@Data
public class DishDto extends Dish {
    private List<DishFlavor> flavors;
    private String categoryName;
    private Integer copies;
}
