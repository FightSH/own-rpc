package com.hao.transport.dto;

import java.io.Serializable;

/**
 * 传输实体
 */
public class RPCRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    //接口名
    private String interfaceName;
    //方法名
    private String methodName;

    private String requestId;

    private Object[] parameters;

    private Class<?>[] parameterTypes;


    public RPCRequest() {
    }

    public RPCRequest(String interfaceName, String methodName, String requestId, Object[] parameters, Class<?>[] parameterTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.requestId = requestId;
        this.parameters = parameters;
        this.parameterTypes = parameterTypes;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
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
