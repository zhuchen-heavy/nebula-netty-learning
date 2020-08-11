package com.nebula.netty.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <p>
 * BIO的client端
 * </p>
 *
 * @author: zhu.chen
 * @date: 2020/8/11
 * @version: v1.0.0
 */
public class BIOClient {

    public static void main(String[] args) {
        try {
            connect("127.0.0.1", 8080);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connect(String hostname, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostname, port));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        // 从键盘输入
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String str = null;
        while ((str = br.readLine()) != null) {
            if ("quit".equals(str)) {
                break;
            }
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
        br.close();
        socket.close();
    }

}