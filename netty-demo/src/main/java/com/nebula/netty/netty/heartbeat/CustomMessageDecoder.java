package com.nebula.netty.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 *  自定义的消息解码器
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/31
 * @version: v1.0.0
 */
public class CustomMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        long id = in.readLong();
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        String content = new String(bytes);
        CustomProtocol customProtocol = new CustomProtocol();
        customProtocol.setId(id);
        customProtocol.setContent(content);
        // 将解码后的数据加入到list中
        out.add(customProtocol);
    }

}
