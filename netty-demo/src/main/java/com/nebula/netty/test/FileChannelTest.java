package com.nebula.netty.test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * <p>
 * FileChannel 读写文件的示例
 *
 * FileChannel 和 FileInputStream区别：
 * 1：不同点
 * FileChannel：基于管道。
 * FileInputStream：基于文件流。
 * 2：相同点：
 * 本质上都是基于native的ReadFile。
 * 就文件复制而言，FileChannel性能会好于FileInputStream，毕竟FileChannel基于内核的sendfile机制，少了用户态和内核态的来回copy的动作。
 * </p>
 *
 * @author zhu.chen
 * @version 1.0
 * @date 2020/8/11
 */
public class FileChannelTest {

    public static void main(String[] args) throws IOException {
        readFile();
        writeFile();
        copy1();
    }

    /**
     * <p>
     * FileChannel 实现文件复制
     * transferTo 和 transferFrom差不多
     * </p>
     */
    private static void copy1() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        RandomAccessFile rs1 = new RandomAccessFile(new File(path + "test.txt"), "rw");
        RandomAccessFile rs2 = new RandomAccessFile(new File(path + "test-copy.txt"), "rw");
        FileChannel fileChannelIn = rs1.getChannel();
        FileChannel fileChannelOut = rs2.getChannel();
        fileChannelIn.transferTo(0, fileChannelIn.size(), fileChannelOut);
    }

    /**
     * <p>
     * FileChannel 写文件
     * </p>
     */
    private static void writeFile() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        RandomAccessFile rs = new RandomAccessFile(new File(path + "test.txt"), "rw");
        FileChannel fileChannel = rs.getChannel();
        byte[] data = "ok".getBytes(Charset.forName("UTF-8"));
        // 将byte[]包装为ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        fileChannel.write(byteBuffer);
    }

    /**
     * <p>
     * FileChannel 读取文件
     * mac的权限问题：不能讲文件放在桌面上，否则会报错Operation not permitted
     * </p>
     */
    private static void readFile() throws IOException {
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        RandomAccessFile rs = new RandomAccessFile(new File(path + "hello world.txt"), "rw");

        FileChannel fileChannel = rs.getChannel();
        // 堆外内存
        //ByteBuffer byteBuffer = ByteBuffer.allocateDirect(48);
        // 堆内内存
        ByteBuffer byteBuffer = ByteBuffer.allocate(48);
        int byteRead = fileChannel.read(byteBuffer);
        while (byteRead != -1) {
            System.out.println("read " + byteRead);
            // 设置 limit 和 position，从buffer的开头读取数据。
            byteBuffer.flip();

            while (byteBuffer.hasRemaining()) {
                System.out.print((char) byteBuffer.get());
            }
            byteBuffer.clear();
            byteRead = fileChannel.read(byteBuffer);
        }
        fileChannel.close();
    }

    /**
     * FileInputStream 和 RandomAccessFile一样，均可以拿到FileChannel
     * @throws FileNotFoundException
     */
    private static void test() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File(""));
        fis.getChannel();
    }

}
