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

}
