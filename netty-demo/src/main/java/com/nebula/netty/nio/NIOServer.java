package com.nebula.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * NIO的server
 * 参考：https://www.cnblogs.com/shihuc/p/6559025.html
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/11
 * @version: v1.0.0
 */
public class NIOServer {

    public static void main(String[] args) throws IOException {
        openServer(Constants.SERVER_PORT);
    }

    private static void openServer(int port) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        // 监听selector的accept事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels <= 0) {
                // 继续下一次循环
                continue;
            }
            // 获取到selector上所有的key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isAcceptable()) { // 处理OP_ACCEPT事件
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                if (selectionKey.isReadable()) { //处理OP_READ事件
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    StringBuilder stringBuilder = new StringBuilder();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int readBytes = 0;
                    int ret = 0;
                    while ((ret = socketChannel.read(byteBuffer)) > 0) {
                        readBytes += ret;
                        byteBuffer.flip();
                        stringBuilder.append(Charset.forName("UTF-8").decode(byteBuffer).toString());
                        byteBuffer.clear();
                    }
                    if (readBytes == 0) {
                        System.err.println("handle opposite close Exception");
                        socketChannel.close();
                    }

                    String message = stringBuilder.toString();
                    System.out.println("Message from client: " + message);
                    if (Constants.CLIENT_CLOSE.equalsIgnoreCase(message.trim())) {
                        System.out.println("Client is going to shutdown!");
                        socketChannel.close();
                    } else if (Constants.SERVER_CLOSE.equalsIgnoreCase(message.trim())) {
                        System.out.println("Server is going to shutdown!");
                        socketChannel.close();
                        serverSocketChannel.close();
                        selector.close();
                        System.exit(0);
                    } else {
                        String outMessage = "Server response：" + message;
                        socketChannel.write(Charset.forName("UTF-8").encode(outMessage));
                    }
                    iterator.remove();
                }
            }
        }

    }

}
















