package com.hao.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();

        socketChannel.configureBlocking(false);

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 9999);

        if (!socketChannel.connect(address)) {
//            System.out.println("connection failed...");


            while (!socketChannel.finishConnect()) {
                System.out.println("thread can do other things");;
            }
        }

        String msg = "hello NIO";

        ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());


        socketChannel.write(byteBuffer);

        System.in.read();


    }

}
