package com.nebula.netty.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.time.LocalDateTime;

/**
 * <p>
 * 对网络事件进行读写操作
 *
 * "line.separator"：换行符
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/13
 * @version: v1.0.0
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    private int counter;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        //String body = new String(req, Charset.forName("UTF-8"));
        System.out.println("time server receive order is : " + body + "; the counter is : " + ++counter);

        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                LocalDateTime.now().toString() : "BAD ORDER";
        /**
         * System.getProperty("line.separator")：空格换行符。
         * 意思：对当前时间加一个空格换行符
         */
        currentTime = currentTime + System.getProperty("line.separator");
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        // 异步回写，为了防止频繁的唤醒Selector进行消息发送，将消息写入到发送消息的缓冲数组中
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}

