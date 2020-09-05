package com.ttk.file.server.config;

import com.ttk.file.server.domain.Resp;
import com.ttk.file.server.domain.enums.RespStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局处理异常
 *
 * @author root
 */
@Slf4j
@RestControllerAdvice("com.ttk.file.server.controller")
public class GlobalHandler {

    @ExceptionHandler(value = {
            Exception.class
    })
    public Resp<Object> handle(Exception ex, HttpServletRequest request) {
        log.error("请求错误,url:{} msg:{}", request.getRequestURI(), ex.getMessage());
        return Resp.ofError(RespStatus.SYS_ERR, ex.toString());
    }
}
