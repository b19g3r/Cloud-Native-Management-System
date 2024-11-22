package com.windsurf.common.core.result;

/**
 * 返回码接口
 */
public interface IResultCode {
    /**
     * 获取消息
     *
     * @return 消息
     */
    String getMsg();

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    int getCode();
}
