package com.zl.web;

import com.zl.netty.CoordinationNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.zl.*"})
public class ZLBasicApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ZLBasicApplication.class, args);
    }

    @Autowired
    private CoordinationNettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }
}
