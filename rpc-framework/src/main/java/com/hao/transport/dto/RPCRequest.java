package com.hao.transport.dto;

import java.io.Serializable;

/**
 * 传输实体
 */
public class RPCRequest implements Serializable {
    //接口名
    private String interfaceName;
    //方法名
    private String methodName;

    public RPCRequest() {
    }

    public RPCRequest(String interfaceName, String methodName) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
