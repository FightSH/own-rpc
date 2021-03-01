package com.hao.common.constant;

public class RPCConstants {

    public static final byte[] MAGIC_NUMBER = {'o', 'r', 'p', 'c'};

    public static final byte RPC_VERSION = 1;

    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final int HEAD_LENGTH = 16;
}
