package com.fit2cloud.common.form.vo;

import com.fit2cloud.common.form.constants.InputType;
import lombok.*;

/**
 * @Author:张少虎
 * @Date: 2022/9/7  9:08 AM
 * @Version 1.0
 * @注释:
 */
@Data
public class Form {
    /**
     * 输入类型
     */
    private InputType inputType;
    /**
     * 字段名称
     */
    private String field;
    /**
     * 提示
     */
    private String label;
    /**
     * 数据
     */
    private Object value;
    /**
     * 是否必填
     */
    private Boolean required;
    /**
     * 默认值
     */
    private Object defaultValue;
    /**
     * 描述
     */
    private String description;

}
