package com.csy.cloud.result;

import lombok.Getter;

/**
 * 使用枚举类定义返回结果，如果不用枚举类，代码里到处都是200，500这种魔法数字，不好维护，也不好扩展
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"操作成功"),
    FAIL(500, "系统异常"),
    SERVICE_ERROR(400, "参数错误"),
    DATA_ERROR(404, "数据不存在");

    private final Integer code;
    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
