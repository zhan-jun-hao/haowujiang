package com.haowujiang.sanguosha.infrastructure.common;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封装result类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {

    private int code;

    private String msg;

    private T data;

    /**
     * 成功返回泛型
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /**
     * 成功不需要返回数据
     *
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 失败不需要返回数据 但是需要返回错误信息
     *
     * @param msg 错误信息
     * @param <T> 数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(500, msg, null);
    }

    /**
     * 失败时需要指定返回的错误码
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    public static <T> Result<T> fail(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
