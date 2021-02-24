package com.hao.transport.dto;

import com.hao.common.constant.RPCStatusCode;

import java.io.Serializable;

public class RPCResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    private String requestId;

    public static <T>RPCResponse<T> success(T data, String requestId) {

        return new RPCResponse<T>(RPCStatusCode.SUCCESS.getCode(), RPCStatusCode.SUCCESS.getMessage(), data, requestId);

    }

    public static <T>RPCResponse<T> failed(T data, String requestId) {

        RPCResponse<T> response = new RPCResponse<T>();
        response.setCode(RPCStatusCode.FAIL.getCode());
        response.setMessage(RPCStatusCode.FAIL.getMessage());
        return response;

    }

    public RPCResponse() {
    }

    public RPCResponse(Integer code, String message, T data, String requestId) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = requestId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    @Override
    public String toString() {
        return "RPCResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
