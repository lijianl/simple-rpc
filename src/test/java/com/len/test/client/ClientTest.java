package com.len.test.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;



public class ClientTest {

    public static void main(String[] args) {
        initBean();
    }


    public static void initClient() {

    }

    // 测试bean的初始化流程
    public static void initBean() {

        ApplicationContext context = new ClassPathXmlApplicationContext("test-bean.xml");

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 测试简单的通信
    public static void testProxy(){

    }
}
