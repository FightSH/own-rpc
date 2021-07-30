package com.hao.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {


    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(9999));
        // selector对象
        Selector selector = Selector.open();
        // 设置非阻塞
        serverSocketChannel.configureBlocking(false);
        // 将serverSocketChannel注册到selector中。关注事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环
        while (true) {
            // 等两秒，若没有连接就返回
            if (selector.select(2000) == 0) {
                System.out.println("server have been waited for 2s...");
                continue;
            }
            //获取到相关 selectionKey集合
            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> keyIterator = keys.iterator();

            while (keyIterator.hasNext()) {
                // 根据key对应的通道发生的事件做相应处理
                SelectionKey key = keyIterator.next();
                // 连接事件
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    // 将socketChannel关联到selector中，关注事件为OP_READ
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }

                if (key.isReadable()) {
                    // 通过key反向获取channel
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();

                    while (socketChannel.read(byteBuffer) != -1) {
                        System.out.println("from client:" + new String(byteBuffer.array()));
                        byteBuffer.clear();

                    }

                }

                keyIterator.remove();
            }

        }

    }
}
