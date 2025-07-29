package com.zl.web.manager.security;

import com.zl.common.constant.UserConstant;
import com.zl.common.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

 /**
 * JWT认证过滤器
 * 在JwtAuthenticationFilter中解析 Token 并设置认证信息到SecurityContext
 * @Author GuihaoLv
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(StringRedisTemplate stringRedisTemplate, JwtUtil jwtUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String userToken = request.getHeader("Authorization");

        if (userToken != null && userToken.startsWith("Bearer ")) {
            userToken = userToken.substring(7); // 提取Token
            String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
            String jwtToken = stringRedisTemplate.opsForValue().get(jwtTokenKey);
            if (jwtToken != null) {
                try {
                    Claims claims = jwtUtil.parseToken(jwtToken);
                    String username = claims.getSubject(); // 假设username是Token中的用户标识

                    // 创建认证对象（需从数据库或Token中获取用户权限）
                    UserDetails userDetails = new UserAuth(/* 从claims或数据库获取用户信息 */);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetails.getUsername(), null, userDetails.getAuthorities()
                    );

                    // 设置到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext(); // 清除无效认证
                }
            }
        }

        filterChain.doFilter(request, response);
    }





}