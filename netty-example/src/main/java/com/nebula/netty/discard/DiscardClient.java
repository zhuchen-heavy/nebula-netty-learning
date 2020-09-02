package com.nebula.netty.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class DiscardClient {

    private static final boolean SSL = System.getProperty("ssl") != null;
    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8009"));
    private static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                p.addLast(sslCtx.newHandler(ch.alloc(), HOST, PORT));
                            }
                            p.addLast(new SimpleChannelInboundHandler<Object>() {
                                private ByteBuf content;
                                private ChannelHandlerContext ctx;

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    this.ctx = ctx;

                                    // Initialize the message.
                                    content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);

                                    // Send the initial messages.
                                    generateTraffic();
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) {
                                    content.release();
                                }

                                @Override
                                public void messageReceived(ChannelHandlerContext ctx, Object msg) {
                                    // Server is supposed to send nothing, but if it sends something, discard it.
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                    // Close the connection when an exception is raised.
                                    cause.printStackTrace();
                                    ctx.close();
                                }

                                long counter;

                                private void generateTraffic() {
                                    // Flush the outbound buffer to the socket.
                                    // Once flushed, generate the same amount of traffic again.
                                    ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
                                }

                                private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) {
                                        if (future.isSuccess()) {
                                            generateTraffic();
                                        } else {
                                            future.cause().printStackTrace();
                                            future.channel().close();
                                        }
                                    }
                                };
                            });
                        }
                    });

            // Make the connection attempt.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
