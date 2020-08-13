package com.nebula.netty.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * <p>
 * NettyTimeServer
 * </p>
 *  Netty实现client-server应答的示例
 *
 * @author: zhu.chen
 * @date: 2020/8/13
 * @version: v1.0.0
 */
public class NettyTimeServer {

    public static void main(String[] args) throws InterruptedException {
        new NettyTimeServer().openServer(8080);
    }

    public void openServer(int port) throws InterruptedException {
        // 接受客户端连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // SocketChannel的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // NIO的服务启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //.handler(new LoggerH)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });
            // 同步等待绑定端口
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 等待服务器监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
