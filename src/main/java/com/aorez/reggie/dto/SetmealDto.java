package com.aorez.reggie.dto;

import com.aorez.reggie.entity.Setmeal;
import com.aorez.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private List<SetmealDish> setmealDishes;
    private String categoryName;
}
