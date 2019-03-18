package com.len.test.server;

import com.len.registry.ServiceRegistry;
import com.len.server.RpcServer;
import com.len.test.common.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcBootstrapWithoutSpring {

    private static final Logger logger = LoggerFactory.getLogger(RpcBootstrapWithoutSpring.class);

    public static void main(String[] args) {

        String serverAddress = "127.0.0.1:18866";
        ServiceRegistry serviceRegistry = new ServiceRegistry("127.0.0.1:2181");
        RpcServer rpcServer = new RpcServer(serverAddress, serviceRegistry);

        HelloService helloService = new HelloServiceImpl();
        rpcServer.addService("com.len.test.common.HelloService", helloService);

        try {

            rpcServer.start();

        } catch (Exception ex) {
            logger.error("Exception: {}", ex);
        }

    }
}
