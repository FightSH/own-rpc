package com.hao.common.constant;

public class RPCConstants {

    public static final byte[] MAGIC_NUMBER = {'o', 'r', 'p', 'c'};

    public static final byte RPC_VERSION = 1;

    public static final String TIK = "tik";
    public static final String TOK = "tok";

    public static final int HEAD_LENGTH = 16;
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int TOTAL_LENGTH = 16;

    //MESSAGE TYPE
    public static final byte REQUEST_TYPE = 1;
    public static final byte RESPONSE_TYPE = 2;
    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;




    public enum CompressTypeEnum {

        GZIP((byte) 0x01, "gzip");

        private final byte code;
        private final String name;


        public static String getName(byte code) {
            for (CompressTypeEnum c : CompressTypeEnum.values()) {
                if (c.getCode() == code) {
                    return c.name;
                }
            }
            return null;
        }

        public byte getCode() {
            return code;
        }

        CompressTypeEnum(byte code, String name) {
            this.code = code;
            this.name = name;
        }

    }


   public enum SerializationTypeEnum {

        KYRO((byte) 0x01, "kyro");

        private final byte code;
        private final String name;

        public static String getName(byte code) {
            for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
                if (c.getCode() == code) {
                    return c.name;
                }
            }
            return null;
        }

        public byte getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        SerializationTypeEnum(byte code, String name) {
            this.code = code;
            this.name = name;
        }
    }


}

