package com.hao.easydemo;

import com.hao.easydemo.service.HelloService;

public class EasyDemoBootstrap {


    public static final String providerName = "HelloService#hello#";

    public static void main(String[] args) {

        EasyDemoRpcClient client = new EasyDemoRpcClient();

        HelloService service = (HelloService) client.getBean(HelloService.class, providerName);

        String result = service.hello("hello rpc");

        System.out.println("message from server" + result);

    }
}
