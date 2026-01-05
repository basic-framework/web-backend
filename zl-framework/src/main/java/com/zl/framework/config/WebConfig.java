package com.zl.framework.config;

import com.zl.framework.interceptor.UserTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;
/**
 * Web 配置类
 * @Author GuihaoLv
 */
@Configuration
@ConfigurationProperties(prefix = "zl.framework.security")
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private UserTokenInterceptor userTokenInterceptor;
    private List<String> ignoreUrl;
    // 必须保留setter方法，用于配置注入
    public void setIgnoreUrl(List<String> ignoreUrl) {
        this.ignoreUrl = ignoreUrl;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTokenInterceptor)
                .addPathPatterns("/**")
//                .excludePathPatterns("/web/login"); // 登录不拦截
                .excludePathPatterns(ignoreUrl.toArray(new String[0]));
    }

    /**
     设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 注册 Knife4j 所需的静态资源
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        // 注册 webjars 所需的静态资源
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }



}
