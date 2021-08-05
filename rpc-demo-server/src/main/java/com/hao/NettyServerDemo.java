package com.hao;

import com.hao.annotation.RPCScan;
import com.hao.registry.RpcServiceProperties;
import com.hao.serviceimpl.HelloServiceImpl2;
import com.hao.transport.netty.server.NettyServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.UnknownHostException;

@RPCScan(basePackage = {"com.hao"})
public class NettyServerDemo {

    public static void main(String[] args) {
        // Register service via annotation
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyServerDemo.class);
        NettyServer nettyRpcServer = (NettyServer) applicationContext.getBean("nettyServer");
        // Register service manually
        HelloService helloService2 = new HelloServiceImpl2();
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties("version-two", "test2", "helloService");

        nettyRpcServer.registerService(helloService2, rpcServiceProperties);
        try {
            nettyRpcServer.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
