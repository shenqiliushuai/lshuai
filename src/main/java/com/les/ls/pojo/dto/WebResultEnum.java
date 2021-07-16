package com.les.ls.pojo.dto;

/**
 * web请求结果枚举
 *
 * @author lshuai
 */
public enum WebResultEnum {

    SUCCESS(0, "成功！"),

    FAILED(-1, "失败！"),

    RESOURCE_NOT_FOUND(404, "无法找到指定资源！"),

    SERVER_ERROR(500, "服务未知异常！");
    /**
     * 响应码
     */
    private final Integer code;
    /**
     * 响应消息
     */
    private final String message;

    WebResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
