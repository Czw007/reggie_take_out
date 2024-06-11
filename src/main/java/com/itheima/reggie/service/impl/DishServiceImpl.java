package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.config.MyThreadPool;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.sun.xml.internal.ws.util.CompletedFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private MyThreadPool myThreadPool;
    /**
     *  新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    // 涉及多表操作，需要加事务，使用@Transactional注解，需要在启动类加@EnableTransactionManagement
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto){
        // 保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        Long dishId=dishDto.getId(); // 菜品id

        // 菜品口味
        List<DishFlavor> flavors=dishDto.getFlavors();
        flavors=flavors.stream().map((item)-> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        // 保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) throws ExecutionException, InterruptedException {
        DishDto dishDto=new DishDto();
        Dish dish=this.getById(id);
        // 任务1
        // 需要返回值使用supplyAsync，不需要返回值可以使用runAsync
        CompletableFuture<String> future1= CompletableFuture.supplyAsync(()->{
            // 查询菜品基本信息，从dish表查询
            BeanUtils.copyProperties(dish,dishDto);
            return "获取菜品信息成功";
        },myThreadPool);

        // 任务2
        // 如果任务2需要依赖任务1的返回结果，可以使用thenAccptedAsync
        CompletableFuture<String> future2=CompletableFuture.supplyAsync(()->{
            // 查询当前菜品对应的口味信息，从dish_flavor表查询
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,dish.getId());
            List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(flavors);
            return "获取口味信息成功";
        },myThreadPool);

        // 等待所有的任务都完成
        CompletableFuture.anyOf(future1,future2).get();

        // 返回dishDto
        return dishDto;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto){
        // 更新dish表基本信息
        this.updateById(dishDto);

        // 清理当前菜品对应口味数据--dish_flavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味数据--dish_flavor表的insert操作
        List<DishFlavor> flavors=dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());


        dishFlavorService.saveBatch(flavors);

    }
}
