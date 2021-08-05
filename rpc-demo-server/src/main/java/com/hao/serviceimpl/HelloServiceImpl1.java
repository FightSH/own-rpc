package com.hao.serviceimpl;

import com.hao.Hello;
import com.hao.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl1 implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl1.class);

    static {
        System.out.println("HelloServiceImpl1被创建");
    }

    @Override
    public String hello(Hello hello) {
        logger.info("HelloServiceImpl1收到: {}.", hello.getMessage());
        String result = "Hello description is " + hello.getDescription();
        logger.info("HelloServiceImpl1返回: {}.", result);
        return result;
    }


}
