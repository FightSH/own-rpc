package com.hao.transport;

import com.hao.spi.RPCSPI;
import com.hao.transport.dto.RPCRequest;

@RPCSPI
public interface TransportInterface {

    Object sendRequest(RPCRequest rpcRequest);

}
