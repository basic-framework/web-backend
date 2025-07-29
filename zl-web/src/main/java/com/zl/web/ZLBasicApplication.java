package com.zl.web;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zl.web", "com.zl.common"})
public class ZLBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZLBasicApplication.class, args);
    }

}
