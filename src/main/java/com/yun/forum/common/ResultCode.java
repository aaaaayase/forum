package com.yun.forum.common;

/**
 * @author yun
 * @date 2024/9/13 18:14
 * @desciption: 定义状态码
 */
public enum ResultCode {
    SUCCESS(0, "成功"),

    FAILED(1000, "操作失败"),

    FAILED_UNAUTHORIZED(1001, "未授权"),

    FAILED_PARAMS_VALIDATE(1002, "参数校验失败"),

    FAILED_FORBIDDEN(1003, "禁止访问"),

    FAILED_CREATE(1004, "新增失败"),

    FAILED_NOT_EXISTS(1005, "资源不存在"),
    // 用户
    FAILED_USER_EXISTS(1101, "用户已存在"),

    FAILED_USER_NOT_EXISTS(1102, "用户不存在"),

    FAILED_LOGIN(1103, "用户名或密码错误"),

    FAILED_USER_BANNED(1104, "您已被禁言，请联系管理员并重新登录"),

    FAILED_TWO_PWD_NOT_SAME(1105, "两次输入的密码不一致"),
    FAILED_USER_ARTICLE_COUNT(1106, "用户更新帖子数量失败"),
    // 版块
    FAILED_BOARD_ARTICLE_COUNT(1201, "版块更新帖子数量失败"),
    FAILED_BOARD_BANNED(1202, "版块已被禁用"),
    FAILED_BOARD_NOT_EXIST(1203, "版块不存在"),

    FAILED_ARTICLE_NOT_EXIST(1301, "帖子不存在"),

    FAILED_ARTICLE_BANNED(1302, "帖子已被禁"),

    ERROR_SERVICES(2000, "服务器内部错误"),

    ERROR_IS_NULL(2001, "IS NULL");

    int code;
    String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
