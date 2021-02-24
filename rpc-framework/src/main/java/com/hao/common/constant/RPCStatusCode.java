package com.hao.common.constant;

public enum RPCStatusCode {

    SUCCESS(200, "The remote procedure call is successful..."),
    FAIL(500, "The remote procedure call is failed...");


    private final int code;

    private final String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }



    RPCStatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
