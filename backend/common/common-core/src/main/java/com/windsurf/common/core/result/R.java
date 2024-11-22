package com.windsurf.common.core.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 统一响应结果
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public static <T> R<T> ok() {
        return restResult(null, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, ResultCode.SUCCESS.getCode(), msg);
    }

    public static <T> R<T> failed() {
        return restResult(null, ResultCode.FAILED.getCode(), ResultCode.FAILED.getMsg());
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, ResultCode.FAILED.getCode(), msg);
    }

    public static <T> R<T> failed(IResultCode resultCode) {
        return restResult(null, resultCode.getCode(), resultCode.getMsg());
    }

    public static <T> R<T> failed(IResultCode resultCode, String msg) {
        return restResult(null, resultCode.getCode(), msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }
}
