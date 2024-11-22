package com.windsurf.common.core.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    
    // 业务异常
    USER_EXISTS(1001, "用户已存在"),
    USER_NOT_EXISTS(1002, "用户不存在"),
    WRONG_PASSWORD(1003, "密码错误"),
    ACCOUNT_LOCKED(1004, "账号已被锁定"),
    
    // 系统异常
    SYSTEM_ERROR(2001, "系统异常"),
    NETWORK_ERROR(2002, "网络异常"),
    DB_ERROR(2003, "数据库异常"),
    FILE_ERROR(2004, "文件处理异常"),
    
    // 接口异常
    API_LIMIT_REACHED(3001, "接口调用次数超限"),
    API_UNAUTHORIZED(3002, "接口未授权"),
    API_METHOD_NOT_ALLOWED(3003, "请求方法不允许"),
    API_BAD_REQUEST(3004, "错误的请求参数");

    private final int code;
    private final String msg;
}
