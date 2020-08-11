package com.nebula.netty.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * <p>
 * NIO的client
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/11
 * @version: v1.0.0
 */
public class NIOClient {

    public static void main(String[] args) throws IOException {
        openClient(Constants.SERVER_ADDRESS, Constants.SERVER_PORT);
    }

    private static void openClient(String serverIp, int serverPort) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(serverIp, serverPort));

        Selector selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        Scanner sc = new Scanner(System.in);
        String cont = null;
        while (true) {
            if (socketChannel.isConnected()) {
                cont = sc.nextLine();
                socketChannel.write(Charset.forName("UTF-8").encode(cont));
                if (cont == null || cont.equalsIgnoreCase(Constants.CLIENT_CLOSE)) {
                    socketChannel.close();
                    selector.close();
                    sc.close();
                    System.out.println("See you, 客户端退出系统了");
                    System.exit(0);
                }
            }
            /*
             * 设置1sec的超时时间，进行IO事件选择操作
             */
            int nSelectedKeys = selector.select(5000);
            if (nSelectedKeys > 0) {
                for (SelectionKey skey : selector.selectedKeys()) {
                    /*
                     * 判断检测到的channel是不是可连接的，将对应的channel注册到选择器上，指定关心的事件类型为OP_READ
                     */
                    if (skey.isConnectable()) {
                        SocketChannel connChannel = (SocketChannel) skey.channel();
                        connChannel.configureBlocking(false);
                        connChannel.register(selector, SelectionKey.OP_READ);
                        connChannel.finishConnect();
                    }
                    /*
                     * 若检测到的IO事件是读事件，则处理相关数据的读相关的业务逻辑
                     */
                    else if (skey.isReadable()) {
                        SocketChannel readChannel = (SocketChannel) skey.channel();
                        StringBuilder sb = new StringBuilder();
                        /*
                         * 定义一个ByteBuffer的容器，容量为1k
                         */
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                        int readBytes = 0;
                        int ret = 0;
                        /*
                         * 注意，对ByteBuffer的操作，需要关心的是flip，clear等。
                         */
                        while ((ret = readChannel.read(byteBuffer)) > 0) {
                            readBytes += ret;
                            byteBuffer.flip();
                            sb.append(Charset.forName("UTF-8").decode(byteBuffer).toString());
                            byteBuffer.clear();
                        }

                        if (readBytes == 0) {
                            System.err.println("handle opposite close Exception");
                            readChannel.close();
                        }
                    }
                }
                /*
                 * 一次监听的事件处理完毕后，需要将已经记录的事件清除掉，准备下一轮的事件标记
                 */
                selector.selectedKeys().clear();
            } else {
                System.err.println("handle select timeout Exception");
                socketChannel.close();
            }
        }
    }

}
