package com.len.server;

import com.len.protocol.RpcDecoder;
import com.len.protocol.RpcEncoder;
import com.len.protocol.RpcRequest;
import com.len.protocol.RpcResponse;
import com.len.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * RPC Server
 * <p>
 * 启动作为远程服务，使用ServiceRegistry与ZK通信
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    // netty 报漏server端口
    private String serverAddress;
    private ServiceRegistry serviceRegistry;

    // 存储接口和实现impl映射关系
    private Map<String, Object> handlerMap = new HashMap<>();

    // 线程池
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 6000L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    public RpcServer(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public RpcServer(String serverAddress, ServiceRegistry serviceRegistry) {
        this.serverAddress = serverAddress;
        this.serviceRegistry = serviceRegistry;
    }


    // 启动注册,通过继承ApplicationContextAware,获取ApplicationContext对象
    // 在bean初始化完成是执行,便于获取所有的bean
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                logger.info("local cache service: {}", interfaceName);
                // 本地保存注册接口和实例bean的映射关系
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    // bean初始化时执行的接口
    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    public RpcServer addService(String interfaceName, Object serviceBean) {
        if (!handlerMap.containsKey(interfaceName)) {
            logger.info("Loading service: {}", interfaceName);
            handlerMap.put(interfaceName, serviceBean);
        }
        return this;
    }

    // 启动bean
    public void start() throws Exception {
        EventLoopGroup eventLoopGroup = null;
        try {
            eventLoopGroup = new NioEventLoopGroup();
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(eventLoopGroup, eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    //handler
                                    .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new ServerHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // server地址
            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            // 同步链接
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("Server started on port {}", port);

            // 存储server信息/data
            if (serviceRegistry != null) {
                // 直接写入了全部的信息,没有对信息做特殊的处理
                serviceRegistry.register(serverAddress);
            }

            // 同步关闭
            future.channel().closeFuture().sync();
        } finally {
            // 优雅的停机->需要释放线程池
            eventLoopGroup.shutdownGracefully().sync();
        }
    }

}
