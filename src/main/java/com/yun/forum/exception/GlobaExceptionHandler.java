package com.yun.forum.exception;

import com.yun.forum.common.AppResult;
import com.yun.forum.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author yun
 * @date 2024/9/13 19:45
 * @desciption: 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobaExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ApplicationException.class)
    public AppResult applicationExceptionHandler(ApplicationException e) {
        // 打印异常信息
        e.printStackTrace(); // 上生产环境之前要进行删除
        // 打印日志
        log.error(e.getMessage());
        if (e.getErrorResult() != null) {
            return e.getErrorResult();
        }
        if (e.getMessage() == null || e.getMessage().equals("")) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }


        return AppResult.failed(e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public AppResult exceptionHandler(Exception e) {
        // 打印异常信息
        e.printStackTrace(); // 上生产环境之前要进行删除
        // 打印日志
        log.error(e.getMessage());
        // 非空校验
        if (e.getMessage() == null || e.getMessage().equals("")) {
            return AppResult.failed(ResultCode.ERROR_SERVICES);
        }
        // 返回异常信息
        return AppResult.failed(e.getMessage());
    }
}
