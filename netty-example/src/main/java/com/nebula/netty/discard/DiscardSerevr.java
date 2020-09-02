package com.nebula.netty.discard;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class DiscardSerevr {

    private static final boolean SSL = System.getProperty("ssl") != null;

    private static final int PORT = Integer.parseInt(System.getProperty("key", "8009"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        final SslContext sslContext;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslContext = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        } else {
            sslContext = null;
        }

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            if (sslContext != null) {
                                ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
                            }
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<Object>() {
                                @Override
                                protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println("msg : " + msg);
//                                    ctx.writeAndFlush(msg);

                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }

                            });
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();

            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
