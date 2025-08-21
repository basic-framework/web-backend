package com.zl.web.manager.security;
import cn.hutool.core.util.ObjectUtil;
import com.zl.common.constant.UserConstant;
import com.zl.common.utils.authUtils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT认证过滤器
 * 负责解析Token并设置认证信息到SecurityContext
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;
    private final String TOKEN_PREFIX = "Bearer ";
    private final String AUTH_HEADER = "Authorization";

    public JwtAuthenticationFilter(StringRedisTemplate stringRedisTemplate, JwtUtil jwtUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 提取并验证令牌
            String jwtToken = extractAndValidateToken(request);
            if (jwtToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2. 解析JWT获取用户信息
            Claims claims = jwtUtil.parseToken(jwtToken);

            // 3. 构建用户认证信息
            Authentication authentication = createAuthentication(claims, request);

            // 4. 设置认证信息到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT认证成功，用户: {}", claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (SignatureException e) {
            log.warn("JWT签名验证失败: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (InvalidClaimException e) {
            log.warn("JWT声明无效: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("JWT认证处理异常", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 提取并验证令牌的有效性
     */
    private String extractAndValidateToken(HttpServletRequest request) {
        // 从请求头获取Authorization
        String authHeader = request.getHeader(AUTH_HEADER);

        // 检查Authorization格式
        if (ObjectUtil.isEmpty(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            log.trace("Authorization头不存在或格式不正确");
            return null;
        }

        // 提取用户令牌
        String userToken = authHeader.substring(TOKEN_PREFIX.length());
        if (ObjectUtil.isEmpty(userToken)) {
            log.warn("用户令牌为空");
            return null;
        }

        // 从Redis获取JWT令牌
        String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
        String jwtToken = stringRedisTemplate.opsForValue().get(jwtTokenKey);

        if (ObjectUtil.isEmpty(jwtToken)) {
            log.warn("Redis中未找到有效的JWT令牌，用户令牌: {}", userToken);
            return null;
        }

        return jwtToken;
    }

    /**
     * 根据JWT声明创建认证对象
     */
    private Authentication createAuthentication(Claims claims, HttpServletRequest request) {
        // 获取用户名
        String username = claims.getSubject();
        if (ObjectUtil.isEmpty(username)) {
            throw new RuntimeException("JWT中未包含用户名信息");
        }

        // 获取用户角色/权限
        List<String> authorities = extractAuthoritiesFromClaims(claims);

        // 转换为Spring Security可识别的权限对象
        List<GrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 创建用户详情对象
        User userDetails = new User(username, "", grantedAuthorities);

        // 创建认证令牌
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, grantedAuthorities);

        // 设置请求详情
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }

    /**
     * 从JWT声明中提取权限信息
     */
    private List<String> extractAuthoritiesFromClaims(Claims claims) {
        // 从claims中获取权限信息，这里假设权限存储在"authorities"字段
        Object authoritiesObj = claims.get("authorities");

        if (authoritiesObj instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<String> authorities = (List<String>) authoritiesObj;
            return authorities;
        }

        // 如果没有权限信息，返回空列表
        log.debug("JWT中未包含权限信息，使用空权限列表");
        return new ArrayList<>();
    }
}
