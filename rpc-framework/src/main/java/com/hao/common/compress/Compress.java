package com.hao.common.compress;

import com.hao.spi.RPCSPI;

@RPCSPI
public interface Compress {


    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);

}
