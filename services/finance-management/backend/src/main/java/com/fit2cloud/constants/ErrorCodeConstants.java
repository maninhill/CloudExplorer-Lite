package com.fit2cloud.constants;

import com.fit2cloud.common.utils.LocaleUtil;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * {@code  @Author:张少虎}
 * {@code  @Date: 2022/8/30  2:36 PM}
 * {@code  @Version 1.0}
 * {@code  @注释: 模块-菜单-操作按钮}
 * 账单模块 3000000
 * - 账单总览:    300100000
 * - 账单明细:    300200000
 * - 自定义账单:  300300000
 * - 分账设置:    300400000
 */
public enum ErrorCodeConstants {
    ;

    /**
     * 提示
     */
    private final String message;
    /**
     * 状态吗
     */
    private final Integer code;

    ErrorCodeConstants(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误提示
     *
     * @return 错误提示文本
     */
    public String getMessage() {
        return LocaleUtil.getMessage(message, message);
    }

    /**
     * 获取错误提示
     *
     * @param args 错误提示参数
     * @return 错误提示文本
     */
    public String getMessage(Object[] args) {
        return LocaleUtil.getMessage(message, args, message);
    }

    /**
     * 获取错误code
     *
     * @return 错误code
     */
    public Integer getCode() {
        return code;
    }
}
