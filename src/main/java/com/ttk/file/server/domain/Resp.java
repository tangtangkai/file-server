package com.ttk.file.server.domain;

import com.ttk.file.server.domain.enums.RespStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author root
 */
@Getter
@Setter
@ToString
public class Resp<T> {

    private int code;

    private String msg;

    private T data;

    public static <T> Resp<T> ofSuccess(T data) {
        Resp<T> resp = new Resp<>();
        resp.setData(data);
        resp.setCode(RespStatus.SUCCESS.getCode());
        resp.setMsg(RespStatus.SUCCESS.getMessage());
        return resp;
    }

    public static <T> Resp<T> ofError(RespStatus status, T data) {
        Resp<T> resp = new Resp<>();
        resp.setData(data);
        resp.setCode(status.getCode());
        resp.setMsg(status.getMessage());
        return resp;
    }
}
