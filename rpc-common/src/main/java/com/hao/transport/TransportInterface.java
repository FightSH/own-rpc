package com.hao.transport;

import com.hao.transport.dto.RPCRequest;

public interface TransportInterface {

    Object sendRequest(RPCRequest rpcRequest);

}
