package com.len.test.server;

import com.len.test.common.HelloService;
import com.len.test.common.Person;
import com.len.server.RpcService;


/**
 * 主要用来注册服务
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {


    @Override
    public String hello(String name) {
        return "RPC-> Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "RPC-> Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
