package com.hao.transport;

import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.netty.NettyClient;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class NettyTransport implements TransportInterface{


    private NettyChannelProvider channelProvider;





    @Override
    public Object sendRequest(RPCRequest rpcRequest) {

        //todo 从配置文件中或配置中心获取ip和端口

        final InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1",9999);
        final Channel channel = channelProvider.get(socketAddress);

        if (channel != null && channel.isActive()) {




        }


        return null;
    }
}
