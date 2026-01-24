package com.zl.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zl.*"})
@MapperScan("com.zl.**.mapper")
public class ZLBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZLBasicApplication.class, args);
    }
}
