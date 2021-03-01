package com.hao.common.constant;

public class RPCConstants {

    public static final byte[] MAGIC_NUMBER = {'o', 'r', 'p', 'c'};

    public static final byte RPC_VERSION = 1;

    //ping
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    //pong
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;

    public static final int HEAD_LENGTH = 16;
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int TOTAL_LENGTH = 16;


    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
}
