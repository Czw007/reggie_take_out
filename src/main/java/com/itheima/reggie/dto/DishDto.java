package com.itheima.reggie.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO 数据参数对象，一般用于展示层和服务层之间的数据传输，前端数据和实体类对不上的时候需要重新封装这么一个类当作数据传输对象
 */
@Data
public class DishDto extends Dish {

    // 菜品口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
