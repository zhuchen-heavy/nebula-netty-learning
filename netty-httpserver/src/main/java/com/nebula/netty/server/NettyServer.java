package com.nebula.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;

/**
 * <p>
 * Netty自定义Http-Server容器
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/27
 * @version: v1.0.0
 */
public class NettyServer {

    private static final int port = 8080;

    private static EventLoopGroup boss = new NioEventLoopGroup();

    private static EventLoopGroup worker = new NioEventLoopGroup();

    public static void main(String[] args) throws InterruptedException {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
                            // 服务端的handler处理
                            ch.pipeline().addLast("handler", new ChannelInboundHandlerAdapter() {

                                private String result = "";

                                /*
                                 * 收到消息时，返回信息
                                 */
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (!(msg instanceof FullHttpRequest)) {
                                        result = "未知请求!";
                                        send(ctx, result, HttpResponseStatus.BAD_REQUEST);
                                        return;
                                    }
                                    FullHttpRequest httpRequest = (FullHttpRequest) msg;
                                    try {
                                        String path = httpRequest.uri();          //获取路径
                                        String body = getBody(httpRequest);     //获取参数
                                        HttpMethod method = httpRequest.method();//获取请求方法
                                        //如果不是这个路径，就直接返回错误
                                        if (!"/test".equalsIgnoreCase(path)) {
                                            result = "非法请求!";
                                            send(ctx, result, HttpResponseStatus.BAD_REQUEST);
                                            return;
                                        }
                                        System.out.println("接收到:" + method + " 请求");
                                        //如果是GET请求
                                        if (HttpMethod.GET.equals(method)) {
                                            //接受到的消息，做业务逻辑处理...
                                            System.out.println("body:" + body);
                                            result = "GET请求";
                                            send(ctx, result, HttpResponseStatus.OK);
                                            return;
                                        }
                                        //如果是POST请求
                                        if (HttpMethod.POST.equals(method)) {
                                            //接受到的消息，做业务逻辑处理...
                                            System.out.println("body:" + body);
                                            result = "POST请求";
                                            send(ctx, result, HttpResponseStatus.OK);
                                            return;
                                        }

                                        //如果是PUT请求
                                        if (HttpMethod.PUT.equals(method)) {
                                            //接受到的消息，做业务逻辑处理...
                                            System.out.println("body:" + body);
                                            result = "PUT请求";
                                            send(ctx, result, HttpResponseStatus.OK);
                                            return;
                                        }
                                        //如果是DELETE请求
                                        if (HttpMethod.DELETE.equals(method)) {
                                            //接受到的消息，做业务逻辑处理...
                                            System.out.println("body:" + body);
                                            result = "DELETE请求";
                                            send(ctx, result, HttpResponseStatus.OK);
                                            return;
                                        }
                                    } catch (Exception e) {
                                        System.out.println("处理请求失败!");
                                        e.printStackTrace();
                                    } finally {
                                        //释放请求
                                        httpRequest.release();
                                    }
                                }

                                /**
                                 * 获取body参数
                                 * @param request
                                 * @return
                                 */
                                private String getBody(FullHttpRequest request) {
                                    ByteBuf buf = request.content();
                                    return buf.toString(CharsetUtil.UTF_8);
                                }

                                private void send(ChannelHandlerContext ctx, String context, HttpResponseStatus status) {
                                    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(context, CharsetUtil.UTF_8));
                                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                                    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                }

                                /*
                                 * 建立连接时，返回消息
                                 */
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
                                    ctx.writeAndFlush("客户端" + InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
                                    super.channelActive(ctx);
                                }
                            });
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            System.out.println("服务端启动成功...");
            channelFuture.channel().closeFuture().sync();
            System.out.println("服务端关闭...");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

}
