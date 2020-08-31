package com.nebula.netty.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  心跳的client端
 * </p>
 * @author: zhu.chen
 * @date: 2020/8/31
 * @version: v1.0.0
 */
public class HeartBeatClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatClient.class);

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {

                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                            ch.pipeline().addLast(new IdleStateHandler(0, 5, 0));
                            ch.pipeline().addLast(new CustomMessageEncoder());
                            ch.pipeline().addLast(new CustomMessageDecoder());
                            ch.pipeline().addLast(new ChannelHandlerAdapter() {

                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    if (evt instanceof IdleStateEvent) {
                                        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                                        if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                                            LOGGER.info("client端已经5s没收到server端消息.");
                                            CustomProtocol heartBeat = new CustomProtocol(1, "ping");
                                            ctx.writeAndFlush(heartBeat).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                        }
                                    }
                                    super.userEventTriggered(ctx, evt);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    CustomProtocol customProtocol = (CustomProtocol) msg;
                                    //从服务端收到消息时被调用
                                    LOGGER.info("client端收到server端消息, msg = {}.", customProtocol.toString());
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
