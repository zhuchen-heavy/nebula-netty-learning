package com.nebula.netty.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * <p>
 *  对网络事件进行读写操作
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/13
 * @version: v1.0.0
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 类似于JDK中的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, Charset.forName("UTF-8"));
        System.out.println("time server receive order is : " + body);

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                LocalDateTime.now().toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        // 异步回写，为了防止频繁的唤醒Selector进行消息发送，将消息写入到发送消息的缓冲数组中
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将缓存数组中的数据写入SocketChannel
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

