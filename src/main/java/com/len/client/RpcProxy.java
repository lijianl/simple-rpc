package com.len.client;


import com.len.protocol.RpcRequest;
import com.len.protocol.RpcResponse;
import com.len.registry.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 客户端代理的简单实现
 */
public class RpcProxy {

    private String serverAddress;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    // 同步
    // 使用动态代理完成-创建新的代理bean
    @SuppressWarnings("unchecked")
    public <T> T createBean(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {


                    //代理的方法
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        /*
                         // 发现服务
                        if (serviceDiscovery != null) {
                            serverAddress = serviceDiscovery.discover();
                        }
                        */

                        String[] array = serverAddress.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        // 发送Rpc调用
                        ProxyHandler handler = new ProxyHandler(host, port);  // 初始化 RPC 客户端
                        RpcResponse response = handler.send(request);         // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应

                        // rpc结果
                        if (response.isError()) {
                            // 错误: 服务端
                            return response.getError();
                        } else {
                            //
                            return response.getResult();
                        }
                    }
                }
        );
    }

}
