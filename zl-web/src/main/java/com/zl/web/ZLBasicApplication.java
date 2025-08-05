package com.zl.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.zl.*"})
public class ZLBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZLBasicApplication.class, args);
    }

}
