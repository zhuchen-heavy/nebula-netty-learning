package com.nebula.netty.netty.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class EchoClient {

    private static final String ECHO_QUERY = "Hi,lilinfeng.welcome to netty.$_";

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {
                                private int counter;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    for (int i = 0; i < 10; i++) {
                                        ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_QUERY.getBytes()));
                                        Thread.sleep(6000);
                                    }
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("this is " + ++counter + " time receive is " + msg);
                                    ctx.flush();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
