package com.nebula.netty.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;

/**
 * <p>
 * FileChannel
 * </p>
 *
 * @author zhu.chen
 * @date 2020/8/11
 * @version 1.0
 */
public class FileChannelTest {

    /**
     * mac的权限问题：不能讲文件放在桌面上，否则会报错Operation not permitted
     */
    public static void main(String[] args) throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        RandomAccessFile rs = new RandomAccessFile(new File(path + "hello world.txt"), "rw");

        FileChannel fileChannel = rs.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(48);

        int byteRead = fileChannel.read(byteBuffer);
        while (byteRead != -1) {
            System.out.println("read " + byteRead);
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                System.out.print((char) byteBuffer.get());
            }
            byteBuffer.clear();
            byteRead = fileChannel.read(byteBuffer);
        }
        fileChannel.close();
    }

}
