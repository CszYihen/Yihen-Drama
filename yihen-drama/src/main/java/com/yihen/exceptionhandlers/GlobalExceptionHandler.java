package com.yihen.exceptionhandlers;


import com.yihen.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler { // 全局异常处理

    // 定义需要捕获的异常
    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception e) {
        // 获取请求
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();

        // 打印日志
        e.printStackTrace();


        log.error("代码出现异常，异常信息为：{} ",e.getMessage());
        return Result.error(e.getMessage());
    }

}
