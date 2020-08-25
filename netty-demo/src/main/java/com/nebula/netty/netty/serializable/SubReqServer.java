package com.nebula.netty.netty.serializable;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * <p>
 *  格式：
 *  server端：.channel(NioServerSocketChannel.class) 、.childHandler(new ChannelInitializer<SocketChannel>(){})
 *  client端：.channel(NioSocketChannel.class) 、.handler(new ChannelInitializer<SocketChannel>(){})
 *
 *  NioServerSocketChannel：监听socket通道。server端使用。
 *  NioSocketChannel：连接数据传输通道。server端和client端都有数据传输通道。
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/25
 * @version: v1.0.0
 */
public class SubReqServer {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // 传输通道
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    SubscribeReq req = (SubscribeReq) msg;
                                    if ("zhuchen".equalsIgnoreCase(req.getUserName())) {
                                        System.out.println("receive req is : " + req.toString());
                                        ctx.writeAndFlush(resp(req.getSubReqId()));
                                    }
                                }

                                private SubscribeResp resp(int subReqId) {
                                    SubscribeResp subscribeResp = new SubscribeResp();
                                    subscribeResp.setSubReqId(subReqId);
                                    subscribeResp.setRespCode(0);
                                    subscribeResp.setDesc("netty book order success.");
                                    return subscribeResp;
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });
                        }
                    });
            // 同步等待绑定端口成功
            ChannelFuture future = serverBootstrap.bind(8080).sync();
            // 等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
