package com.ttk.file.server.domain.enums;

import lombok.Getter;

/**
 * @author root
 */

@Getter
public enum RespStatus {

    SUCCESS(1000, "请求成功"),

    SYS_ERR(5000, "系统错误");

    private final int code;

    private final String message;

    RespStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
