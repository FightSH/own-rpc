package com.hao.transport;

import com.hao.common.constant.RPCConstants;
import com.hao.common.factory.SingletonFactory;
import com.hao.registry.ServiceDiscovery;
import com.hao.spi.ExtensionLoader;
import com.hao.transport.channelprovider.NettyChannelProvider;
import com.hao.transport.channelprovider.UnprocessedRequest;
import com.hao.transport.dto.RPCMessage;
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

    private ServiceDiscovery serviceDiscovery;

    private NettyChannelProvider channelProvider;

    private UnprocessedRequest unprocessedRequest;

    public NettyTransport() {
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zookeeper");
        channelProvider = SingletonFactory.getInstance(NettyChannelProvider.class);
        unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    }

    @Override
    public CompletableFuture<RPCResponse<Object>> sendRequest(RPCRequest rpcRequest) {

        final InetSocketAddress inetSocketAddress = serviceDiscovery.discoveryService(rpcRequest.toRpcProperties().toRpcServiceInfo());

        final Channel channel = channelProvider.get(inetSocketAddress);

        CompletableFuture<RPCResponse<Object>> future = new CompletableFuture<>();

        if (channel != null && channel.isActive()) {

            unprocessedRequest.put(rpcRequest.getRequestId(), future);


            final RPCMessage rpcMessage = new RPCMessage();
            rpcMessage.setCodec(RPCConstants.SerializationTypeEnum.KYRO.getCode());
            rpcMessage.setCompress(RPCConstants.CompressTypeEnum.GZIP.getCode());
            rpcMessage.setMessageType(RPCConstants.REQUEST_TYPE);
            rpcMessage.setData(rpcRequest);


            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future1 -> {

                if (future1.isSuccess()) {
                    logger.info("send success");

                } else {
                    future1.channel().close();
                    future.completeExceptionally(future1.cause());
                    logger.error("send failed:", future1.cause());

                }

            });


        } else {
            throw new IllegalStateException();
        }


        return future;
    }
}
