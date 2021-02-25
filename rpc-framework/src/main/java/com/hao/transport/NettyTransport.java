package com.hao.transport;

import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.dto.RPCRequest;
import com.hao.transport.dto.RPCResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

public class NettyTransport implements TransportInterface {

    private static final Logger logger = LoggerFactory.getLogger(NettyTransport.class);

    private NettyChannelProvider channelProvider;

    private UnprocessedRequest unprocessedRequest;


    @Override
    public Object sendRequest(RPCRequest rpcRequest) {

        //todo 从配置文件中或配置中心获取ip和端口

        final InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 9999);

        final Channel channel = channelProvider.get(socketAddress);

        CompletableFuture<RPCResponse<Object>> future = new CompletableFuture<>();

        if (channel != null && channel.isActive()) {

            unprocessedRequest.put(rpcRequest.getRequestId(), future);


            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener)future1 -> {

                if (future1.isSuccess()) {
                    logger.info("send success");

                } else {
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                    logger.error("send failed:",future1.cause());

                }

            });


        }


        return future;
    }
}
