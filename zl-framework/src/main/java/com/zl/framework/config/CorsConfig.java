package com.zl.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有接口都允许跨域
                .allowedOrigins("http://localhost:5175")  // 允许前端域名
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 必须包含 OPTIONS（预检请求方法）
                .allowedHeaders("*")  // 允许所有请求头（如 Token、Content-Type）
                .allowCredentials(true)  // 允许携带 Cookie（如登录凭证）
                .maxAge(3600);  // 预检请求缓存 1 小时（减少重复检查）
    }
}