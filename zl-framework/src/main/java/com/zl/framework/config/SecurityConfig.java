package com.zl.framework.config;

import com.zl.common.properties.SecurityConfigProperties;
import com.zl.framework.filter.JwtAuthenticationFilter;
import com.zl.framework.manager.security.JwtAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 *  权限核心配置类
 *  @Author GuihaoLv
 */
@Configuration
@EnableWebSecurity // 显式启用Web安全配置（Spring Security 6.x 推荐添加，增强可读性）
@EnableConfigurationProperties(SecurityConfigProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级权限控制（@PreAuthorize等注解生效）
public class SecurityConfig {

    @Autowired
    private SecurityConfigProperties securityConfigProperties;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // 注入JWT认证过滤器
//    @Autowired
//    JwtAuthorizationManager jwtAuthorizationManager;

    /**
     * 核心安全过滤链配置
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 获取无需认证的接口路径列表
        List<String> ignoreUrl = securityConfigProperties.getIgnoreUrl();

        http
                // 1. 配置安全匹配器，对所有请求生效
                .securityMatcher("/**")
                // 2. 添加JWT过滤器，置于UsernamePasswordAuthenticationFilter之前
                // 作用：优先解析JWT令牌，完成用户认证
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 3. 授权请求配置
                .authorizeHttpRequests(authorize -> authorize
                        // 无需认证的接口，直接放行
                        .requestMatchers(ignoreUrl.toArray(new String[0]))
                        .permitAll()
                        // 其他所有请求，只需完成认证（授权交给方法级注解@PreAuthorize等处理）
                        .anyRequest()
//                        .access(jwtAuthorizationManager) 全局统一授权
                        .authenticated() // 只需要认证，不在这里进行授权，启用方法级授权（@PreAuthorize）
                )
                // 4. 禁用CSRF（前后端分离项目，无会话状态，无需CSRF保护）
                .csrf(csrf -> csrf.disable())
                // 5. 配置会话策略为无状态（JWT认证无需创建HttpSession）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 可选：禁用缓存控制（避免接口响应被浏览器缓存）
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable())
                );

        return http.build();
    }

    /**
     * 跨域配置源（解决前后端分离跨域问题）
     * @return CorsConfigurationSource 跨域配置
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有来源（生产环境可指定具体域名，如https://xxx.com）
        configuration.addAllowedOriginPattern("*");
        // 允许所有HTTP请求方法（GET/POST/PUT/DELETE等）
        configuration.addAllowedMethod("*");
        // 允许所有请求头
        configuration.addAllowedHeader("*");
        // 允许携带认证信息（如Cookie、JWT令牌）
        configuration.setAllowCredentials(true);
        // 配置跨域缓存时间（预检请求有效期，单位：秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有请求生效跨域配置
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 认证管理器（用于用户账号密码登录认证）
     * @param authenticationConfiguration 认证配置对象
     * @return AuthenticationManager 认证管理器
     * @throws Exception 配置异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * BCrypt密码编码器（用于密码加密与校验）
     * @return BCryptPasswordEncoder 密码编码器
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}