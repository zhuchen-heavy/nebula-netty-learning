package com.nebula.netty.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>
 * InetSocketAddress
 * </p>
 * InetSocketAddress和InetAddress的区别
 * 1：InetSocketAddress：实现Socket地址(IP 地址 + 端口号)，不依赖任何协议。主要用在网络协议中。
 * 2：InetAddress：Java中对IP的封装，代表互联网协议(IP)地址。
 *
 * @author: zhu.chen
 * @date: 2020/8/11
 * @version: v1.0.0
 */
public class InetSocketAddressTest {

    public static void main(String[] args) throws UnknownHostException {
        //inetAddressTest();
    }

    private static void inetAddressTest() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        //
        System.out.println(address.getHostName());
        // ip 地址
        System.out.println(address.getHostAddress());
        System.out.println(address.getAddress());
        System.out.println(address.getCanonicalHostName());
    }

}
