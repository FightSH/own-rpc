package com.hao.transport.channelprovider;

import com.hao.common.factory.SingletonFactory;
import com.hao.transport.netty.NettyClient;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettyChannelProvider {

    private final NettyClient nettyClient;

    private final Map<String, Channel> channelMap;


    public NettyChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
        nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }


    public Channel get(InetSocketAddress inetSocketAddress) {
        final String key = inetSocketAddress.toString();

        if (channelMap.containsKey(key)) {
            final Channel channel = channelMap.get(key);

            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }

        }

        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key, channel);
        return channel;

    }

}
