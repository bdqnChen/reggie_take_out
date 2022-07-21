package com.bdqn.reggie.dto;

import com.bdqn.reggie.pojo.Setmeal;
import com.bdqn.reggie.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
