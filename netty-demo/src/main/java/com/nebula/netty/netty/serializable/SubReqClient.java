package com.nebula.netty.netty.serializable;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class SubReqClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(boss)
                    // NioSocketChannel：client使用，作为子通道；NioServerSocketChannel：server使用，作为父通道
                    .channel(NioSocketChannel.class)
                    // 关闭nagle算法，TCP的优化算法，默认为false，true：关闭。false：开启
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ObjectDecoder(1024 * 1024,
                                    ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    for (int i = 0; i < 10; i++) {
                                        ctx.write(subscribeResp(i));
                                    }
                                    ctx.flush();
                                }

                                private SubscribeReq subscribeResp(int i) {
                                    SubscribeReq subscribeReq = new SubscribeReq();
                                    subscribeReq.setAddress("杭州西湖");
                                    subscribeReq.setPhoneNumber("138xxxxxxxx");
                                    subscribeReq.setProductName("netty");
                                    subscribeReq.setSubReqId(i);
                                    subscribeReq.setUserName("zhuchen");
                                    return subscribeReq;
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    SubscribeResp resp = (SubscribeResp) msg;
                                    System.out.println("resp is : " + resp.toString());
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
        }
    }

}
