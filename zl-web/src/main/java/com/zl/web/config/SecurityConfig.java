package com.zl.web.config;

import com.zl.common.properties.SecurityConfigProperties;
import com.zl.web.manager.security.JwtAuthenticationFilter;
import com.zl.web.manager.security.JwtAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    @Author GuihaoLv
 */
@Configuration
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class SecurityConfig  {

    @Autowired
    SecurityConfigProperties securityConfigProperties;

    @Autowired
    JwtAuthorizationManager jwtAuthorizationManager;

     @Autowired
     private JwtAuthenticationFilter jwtAuthenticationFilter; // 注入过滤器

     @Bean
     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         List<String> ignoreUrl = securityConfigProperties.getIgnoreUrl();

         http
                 .securityMatcher("/**")
                 .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // 添加过滤器
                 .authorizeHttpRequests(authorize -> authorize
                         .requestMatchers(ignoreUrl.toArray(new String[0]))
                         .permitAll()
                         .anyRequest()
                         .access(jwtAuthorizationManager)
                 )
                 .csrf(csrf -> csrf.disable())
                 .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

         return http.build();
     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         // 忽略地址
//         List<String> ignoreUrl = securityConfigProperties.getIgnoreUrl();
//
//         http.securityMatcher("/**")
//                 .authorizeHttpRequests(authorize -> authorize
//                         .requestMatchers(ignoreUrl.toArray(new String[0]))
//                         .permitAll()
//                         .anyRequest()
//                         .access(jwtAuthorizationManager)
//                 );
//
//         // 使用新的API替代弃用的方法
//         http.csrf(csrf -> csrf.disable());
//         http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//         http.headers(headers -> headers.cacheControl(cache -> cache.disable()));
//
//         return http.build();
//     }

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*"); // 允许所有来源
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }

     /**
     * BCrypt密码编码
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
