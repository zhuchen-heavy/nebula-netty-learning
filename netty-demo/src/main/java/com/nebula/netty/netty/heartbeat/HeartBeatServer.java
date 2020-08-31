package com.nebula.netty.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 心跳的server端
 * </p>
 * https://github.com/crossoverJie/cim
 * https://crossoverjie.top/2018/05/24/netty/Netty(1)TCP-Heartbeat/
 *
 * 注：编解码器需要out在in前面，out ---> 输出，in ---> 输入
 *
 * @author: zhu.chen
 * @date: 2020/8/31
 * @version: v1.0.0
 */
public class HeartBeatServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatServer.class);

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                            ch.pipeline().addLast(new CustomMessageEncoder());
                            ch.pipeline().addLast(new CustomMessageDecoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {

                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if (evt instanceof IdleStateEvent) {
                                        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                                        if (idleStateEvent.state() == IdleState.READER_IDLE) {
                                            LOGGER.info("server端已经5s没收到client端消息.");
                                            ctx.writeAndFlush(new CustomProtocol(2, "pong")).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                        }
                                    }
                                    super.userEventTriggered(ctx, evt);
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    NettySocketHolder.remove((NioSocketChannel) ctx.channel());
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    CustomProtocol customProtocol = (CustomProtocol) msg;
                                    LOGGER.info("server端收到client端消息, msg = {}.", customProtocol.toString());
                                    //保存客户端与 Channel 之间的关系
                                    NettySocketHolder.put(customProtocol.getId(), (NioSocketChannel) ctx.channel());
                                }
                            });
                        }

                    });
            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

}
