package com.yun.forum.common;

/**
 * @author yun
 * @date 2024/9/13 18:32
 * @desciption: 定义返回结果
 */
public class AppResult<T> {

    // 状态码
    private int code;
    // 返回信息
    private String message;
    // 返回数据
    private T data;

    /**
     * 成功
     *
     * @return
     */
    public static AppResult success() {
        return new AppResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    public static AppResult success(String message) {
        return new AppResult(ResultCode.SUCCESS.getCode(), message);
    }

    public static <T> AppResult<T> success(T data) {
        return new AppResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 失败
     *
     * @return
     */
    public static AppResult failed() {
        return new AppResult<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage());
    }

    public static AppResult failed(String message) {
        return new AppResult<>(ResultCode.FAILED.getCode(), message);
    }

    public static AppResult failed(ResultCode resultCode) {
        return new AppResult<>(resultCode.getCode(), resultCode.getMessage());
    }

    public int getCode() {
        return code;
    }

    public AppResult(int code, String message) {
        this(code, message, null);
    }

    public AppResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
