package com.len.test.common;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 测试bean的初始化
 */
public class TestInitializingBean implements ApplicationContextAware, InitializingBean {

    // bean初始化调用的函数
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean.afterPropertiesSet");
    }

    // 在afterPropertiesSet之后调用
    public void initXML() {
        System.out.println("init-method.initXML");
    }

    // 获取ApplicationContext
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("ApplicationContextAware.setApplicationContext");
    }
}
