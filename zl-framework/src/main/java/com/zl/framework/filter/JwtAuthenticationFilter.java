package com.zl.framework.filter;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.ObjectUtil;
import com.zl.common.constant.UserConstant;
import com.zl.common.properties.SecurityConfigProperties;
import com.zl.common.utils.authUtils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(SecurityConfigProperties.class)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;
    private final String TOKEN_PREFIX = "Bearer ";
    private final String AUTH_HEADER = "Authorization";
    @Autowired
    private SecurityConfigProperties securityConfigProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    public JwtAuthenticationFilter(StringRedisTemplate stringRedisTemplate, JwtUtil jwtUtil) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 核心方法：指定哪些路径不需要经过当前过滤器（对应拦截器的excludePathPatterns）
     * 返回true = 不过滤（放行），返回false = 需要过滤
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        // 遍历排除路径列表，匹配到则放行
        for (String ignoreUrl : securityConfigProperties.getIgnoreUrl()) {
            // 修正：使用手动创建的 pathMatcher 实例进行匹配（解决 DEFAULT 无法解析问题）
            if (pathMatcher.match(ignoreUrl, requestURI)) {
                return true; // 放行，不执行当前过滤器逻辑
            }
        }
        // 非排除路径，需要执行过滤器逻辑
        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 核心修改：把Authentication改为UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken authentication = null;
        try {
            // 1. 提取并验证令牌
            String jwtToken = extractAndValidateToken(request);
            if (jwtToken != null) {
                // 2. 解析JWT获取用户信息
                Claims claims = jwtUtil.parseToken(jwtToken);
                // 3. 构建用户认证信息（原有逻辑不变）
                authentication = (UsernamePasswordAuthenticationToken) createAuthentication(claims, request);
                log.debug("JWT认证成功，用户: {}", claims.getSubject());
            }
        } catch (Exception e) {
            log.warn("JWT认证失败，使用匿名认证: {}", e.getMessage());
        }

        // 核心：如果认证信息为空，设置匿名认证
        //Token解析失败时设置「匿名认证」→ 放行到拦截器
        if (authentication == null) {
            authentication = new UsernamePasswordAuthenticationToken(
                    "anonymousUser", // 匿名用户名
                    null,            // 凭证
                    new ArrayList<>()// 空权限
            );
            // 现在能正常调用setDetails方法了
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }

        // 4. 强制设置认证信息到SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 继续执行过滤器链
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
