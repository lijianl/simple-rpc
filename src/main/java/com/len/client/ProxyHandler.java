package com.len.client;

import com.len.protocol.RpcDecoder;
import com.len.protocol.RpcEncoder;
import com.len.protocol.RpcRequest;
import com.len.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 同时完成了netty客户端启动类和客户端请求处理handler的功能
 */
public class ProxyHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    private String host;
    private int port;

    private RpcResponse response;

    private final Object obj = new Object();

    public ProxyHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
        synchronized (obj) {
            // 收到响应,唤醒等待send
            obj.notifyAll();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }


    /**
     * 每次调用都需要完成netty链接, 没有复用短链接
     */
    public RpcResponse send(RpcRequest request) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class))  // 将 RPC 请求进行编码（为了发送请求）
                                    .addLast(new RpcDecoder(RpcResponse.class)) // 将 RPC 响应进行解码（为了处理响应）
                                    .addLast(ProxyHandler.this);                // 使用当前ProxyHandle实例处理请求
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            // 新建立链接
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().writeAndFlush(request).sync();

            // 等待结果,等待被唤醒
            synchronized (obj) {
                obj.wait();  // 未收到响应，使线程等待
            }

            // 关闭
            if (response != null) {
                future.channel().closeFuture().sync();
            }
            return response;

        } finally {
            // 雅关机
            group.shutdownGracefully();
        }
    }
}
