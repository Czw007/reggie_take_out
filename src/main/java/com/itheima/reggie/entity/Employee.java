package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    // 属性名称需要和数据库字段保持一致
    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    // 在application中设置了map-underscore-to-camel-case: true，按照驼峰命名法映射
    private String idNumber;  //身份证号码

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;

}
