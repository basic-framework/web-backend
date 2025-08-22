package com.zl.framework.config;

import com.zl.web.interceptor.UserTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

 /**
 * Web 配置类
 * @Author GuihaoLv
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private UserTokenInterceptor userTokenInterceptor;

     @Override
     public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(userTokenInterceptor)
                 .addPathPatterns("/**")
                 .excludePathPatterns("/web/login"); // 登录不拦截

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

     // 新增：配置默认Servlet
//     @Override
//     public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//         configurer.enable("default"); // 指定默认Servlet名称为"default"
//     }

}
