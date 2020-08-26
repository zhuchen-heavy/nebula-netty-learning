package com.nebula.netty.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ByteBufTest {

    public static void main(String[] args) {
        ByteBuf buf = Unpooled.buffer(10);
        buf.writeInt(1024);
        buf.writeByte((byte) 1);
        buf.writeByte((byte) 0);

        System.out.println(buf.readInt());
        System.out.println(buf.readByte());
    }

}
