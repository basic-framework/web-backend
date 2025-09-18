package com.zl.framework.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

 /**
 * Knife4j 配置类
 * @Author GuihaoLv
 */
@Configuration
@EnableKnife4j
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("智领软件公司基础系统后端接口")
                        .version("1.0.0")
                        .description("后端接口文档"));
    }



}