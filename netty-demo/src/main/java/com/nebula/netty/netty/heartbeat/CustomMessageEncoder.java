package com.nebula.netty.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>
 *  自定义消息编码器
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/31
 * @version: v1.0.0
 */
public class CustomMessageEncoder extends MessageToByteEncoder<CustomProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, CustomProtocol msg, ByteBuf out) throws Exception {
        out.writeLong(msg.getId());
        out.writeBytes(msg.getContent().getBytes());
    }

}
