package com.fit2cloud.common.constants;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Author:张少虎
 * @Date: 2022/10/8  4:49 PM
 * @Version 1.0
 * @注释:
 */
public enum JobTypeConstants {
    /**
     * 云账户同步任务
     */
    CLOUD_ACCOUNT_SYNC_JOB(0);
    @EnumValue
    private final int code;

    JobTypeConstants(int code) {
        this.code = code;
    }
}
