package com.trust.common.core.api;

import java.io.Serializable;

public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;
    private String traceId;

    public static <T> R<T> success(T data, String traceId) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMsg("success");
        r.setData(data);
        r.setTraceId(traceId);
        return r;
    }

    public static <T> R<T> fail(int code, String msg, String traceId) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setTraceId(traceId);
        return r;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
