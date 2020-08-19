package com.nebula.netty.oio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * 每个线程一个连接
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/19
 * @version: v1.0.0
 */
public class ConnectionPerThread implements Runnable {

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(80);
            while (!Thread.interrupted()) {
                Socket socket = serverSocket.accept();
                Handler handler = new Handler(socket);
                // 新建线程来处理socket
                new Thread(handler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static class Handler implements Runnable {

        final Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    byte[] input = new byte[100];
                    // 将结果写入byte的buffer缓冲区
                    socket.getInputStream().read(input);
                    byte[] output = null;
                    socket.getOutputStream().write(output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
