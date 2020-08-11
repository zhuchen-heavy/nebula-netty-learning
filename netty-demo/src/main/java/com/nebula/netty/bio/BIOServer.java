package com.nebula.netty.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * <p>
 * BIO的server端
 * 参考：https://blog.csdn.net/Luck_ZZ/article/details/95649985
 * </p>
 * 1：Java的IO流和File流是不同的API
 * File流：java.io.FileInputStream
 * IO流：java.io.InputStreamReader
 *
 * @author: zhu.chen
 * @date: 2020/8/11
 * @version: v1.0.0
 */
public class BIOServer {

    public static void main(String[] args) throws IOException {
        openServer(8080);
    }

    private static void openServer(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(port));
        System.out.println("bio server启动");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("连接成功");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String str = null;
            while ((str = br.readLine()) != null) {
                if ("quit".equals(str)) {
                    break;
                }
                System.out.println(str);
            }
            br.close();
            socket.close();
            System.out.println("连接结束");
        }
    }

}
