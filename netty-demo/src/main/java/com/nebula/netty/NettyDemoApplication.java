package com.nebula.netty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class NettyDemoApplication {

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication.run(NettyDemoApplication.class, args);
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address.getHostAddress());
        System.out.println(address.getAddress());
        System.out.println(address.getCanonicalHostName());
        System.out.println(address.getHostName());

    }

}
