# RPC 

## 基础

- RPC，即 Remote Procedure Call（远程过程调用），调用远程的服务
- 实现方式
    - web service, 基于http的RPC,
    - dubbo, 基于TCP的RPC
- 序列化技术
    - protobuf,kryo,Hession,Jackson,以及JDK默认的序列化技术
    - protobuf:面向POJO,不需要.proto文件
    - Objenesis: 实例化对象
    - FastClass: 实现invoke
- NIO 技术
    - netty
- 集群服务的管理服务注册/发现
    - zookeeper

## 总体的设计


## 相关技术

### bean的初始化

```
// spring加载bean的源码:AbstractAutowiredCapableBeanFactory

protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd) throws Throwable {
    
    //判断该bean是否实现了实现了InitializingBean接口，如果实现了InitializingBean接口，则只掉调用bean的afterPropertiesSet方法
    boolean isInitializingBean = (bean instanceof InitializingBean);
    
    if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
        if (logger.isDebugEnabled()) {
            logger.debug("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
        }
         
        if (System.getSecurityManager() != null) {
            try {
                // 授权调用
                AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        //直接调用afterPropertiesSet
                        ((InitializingBean) bean).afterPropertiesSet();
                        return null;
                    }
                },getAccessControlContext());
            } catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        } else {
            //直接调用afterPropertiesSet
            ((InitializingBean) bean).afterPropertiesSet();
        }
    }
    
    if (mbd != null) {
        String initMethodName = mbd.getInitMethodName();
        //判断是否指定了init-method方法，如果指定了init-method方法，则再调用制定的init-method
        if (initMethodName != null && !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&!mbd.isExternallyManagedInitMethod(initMethodName)) {
            //进一步查看该方法的源码，可以发现init-method方法中指定的方法是通过反射实现
            invokeCustomInitMethod(beanName, bean, mbd);
        }
    }
}

```
- bean 初始化方法2中:InitializingBean(接口实现); xml的init-method(反射实现)
- InitializingBean先调用   

### ApplicationContextAware在bean被获取ApplicationContext的信息

```
// 源码—调用关系

```           
    