package com.len.test.client;


import com.len.client.RpcProxy;
import com.len.test.common.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:proxy-spring.xml")
public class PrcxyTest {

    private static final Logger logger = LoggerFactory.getLogger(PrcxyTest.class);


    // 基于动态代理实现
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest() {
        HelloService helloService = rpcProxy.createBean(HelloService.class);
        String result = helloService.hello("World");
        logger.info("rpcProxy-{}", result);
    }
}
