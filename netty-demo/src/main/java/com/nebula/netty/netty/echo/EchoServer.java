package com.nebula.netty.netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoServer {

    // https://juejin.im/post/6844904083032113159
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    /**
                     * ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，
                     * 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，
                     * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，
                     * 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                     */
                    .option(ChannelOption.SO_BACKLOG, 100)
                    //.handler(null)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 以"$_"作为分隔符
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            //ch.pipeline().addLast(new FixedLengthFrameDecoder(20));
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {
                                int counter = 0;

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    // 该对象中的msg已经经过 DelimiterBasedFrameDecoder、StringDecoder解码处理了。
                                    String body = (String) msg;
                                    System.out.println("this is " + ++counter + " time receive. " + body);
                                    body += "$_";
                                    ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
                                    ctx.writeAndFlush(echo);
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            // 添加监听器，在future完成后，调用
            future.addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("future绑定是否成功：" + future.isSuccess());
                    System.out.println("完成了。");
                }

            });
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
