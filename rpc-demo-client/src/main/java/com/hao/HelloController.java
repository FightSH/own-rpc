package com.hao;

import com.hao.annotation.RPCReference;
import org.springframework.stereotype.Component;

@Component
public class HelloController {

    @RPCReference(version = "version-two", group = "test2", proxyType = "jdk")
    private HelloService helloService;

    public void test() throws InterruptedException {
        String hello = this.helloService.hello(new Hello("111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        assert "Hello description is 222".equals(hello);
        Thread.sleep(12000);
        for (int i = 0; i < 10; i++) {
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}
