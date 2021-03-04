package com.hao.common.constant;

public enum RPCErrorMessageEnum {

    NOT_FOUND_NEED_SERVICE("未找到指定服务");


    private final String message;

    RPCErrorMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
