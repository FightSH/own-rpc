package com.hao.common.compress;

public interface Compress {


    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);

}
