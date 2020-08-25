package com.nebula.netty.netty.time;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * <p>
 * NettyTimeServer
 * </p>
 * Netty实现client-server应答的示例
 * <p>
 * LineBasedFrameDecoder + StringDecoder：按行切换的文本解码器，被设计用来支持TCP的拆包和粘包
 * LineBasedFrameDecoder：对"\n"和"\r\n"进行解码
 * StringDecoder：直接将ByteBuf对象解码为String对象
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
        // 接受客户端连接 (父线程池)
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // SocketChannel的网络读写 (子线程池)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // NIO的服务启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    /**
                     * 设置server端IO通道为NIO。
                     * 若要设置为BIO，则为：OioServerSocketChannel.class
                     */
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    /**
                     * .childOption()：给子通道设置一些选项
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    });
            // 同步等待绑定端口
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 等待服务器监听端口关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            // 优雅的关闭连接
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
