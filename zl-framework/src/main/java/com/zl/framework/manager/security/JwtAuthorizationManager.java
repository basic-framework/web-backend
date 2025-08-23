package com.zl.framework.manager.security;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zl.common.constant.UserConstant;
import com.zl.common.properties.JwtProperties;
import com.zl.common.utils.authUtils.JwtUtil;
import com.zl.model.vo.LoginVo;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

 /**
 * 授权管理器
 * @Author GuihaoLv
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final StringRedisTemplate stringRedisTemplate;
    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext requestContext) {
        try {
            // 1. 提取请求信息
            String requestMethodAndPath = extractRequestInfo(requestContext);

            // 2. 验证令牌存在性
            String userToken = extractUserToken(requestContext);
            if (!isTokenValid(userToken)) {
                log.warn("请求令牌不存在或无效，路径: {}", requestMethodAndPath);
                return new AuthorizationDecision(false);
            }

            // 3. 获取并验证JWT令牌
            String jwtToken = getJwtTokenFromRedis(userToken);
            if (!isJwtTokenValid(jwtToken)) {
                log.warn("JWT令牌无效或已过期，用户令牌: {}", userToken);
                return new AuthorizationDecision(false);
            }

            // 4. 解析JWT并获取用户信息
            LoginVo loginVo = parseUserInfoFromJwt(jwtToken);
            if (loginVo == null) {
                log.warn("无法从JWT令牌解析用户信息，用户令牌: {}", userToken);
                return new AuthorizationDecision(false);
            }

            // 5. 验证用户令牌一致性（防止用户被踢下线）
            if (!isUserTokenConsistent(userToken, loginVo)) {
                log.warn("用户令牌不一致，可能已被踢下线，用户名: {}", loginVo.getUsername());
                return new AuthorizationDecision(false);
            }

            // 6. 处理令牌续期
            renewTokenIfNecessary(userToken, jwtToken, loginVo);

            // 7. 验证资源访问权限
            if (hasResourceAccessPermission(loginVo, requestMethodAndPath)) {
                log.debug("用户{}有权访问资源: {}", loginVo.getUsername(), requestMethodAndPath);
                return new AuthorizationDecision(true);
            }

            log.warn("用户{}无权访问资源: {}", loginVo.getUsername(), requestMethodAndPath);
            return new AuthorizationDecision(false);

        } catch (Exception e) {
            log.error("授权校验过程发生异常", e);
            return new AuthorizationDecision(false);
        }
    }

    /**
     * 提取请求方法和路径信息
     */
    private String extractRequestInfo(RequestAuthorizationContext requestContext) {
        String method = requestContext.getRequest().getMethod();
        String requestURI = requestContext.getRequest().getRequestURI();
        return method + requestURI;
    }

    /**
     * 从请求头中提取用户令牌
     */
    private String extractUserToken(RequestAuthorizationContext requestContext) {
        return requestContext.getRequest().getHeader(UserConstant.USER_TOKEN);
    }

    /**
     * 检查令牌是否有效（非空）
     */
    private boolean isTokenValid(String userToken) {
        return !ObjectUtil.isEmpty(userToken);
    }

    /**
     * 从Redis获取JWT令牌
     */
    private String getJwtTokenFromRedis(String userToken) {
        String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
        return stringRedisTemplate.opsForValue().get(jwtTokenKey);
    }

    /**
     * 检查JWT令牌是否有效
     */
    private boolean isJwtTokenValid(String jwtToken) {
        if (ObjectUtil.isEmpty(jwtToken)) {
            return false;
        }
        // 这里可以添加更多JWT格式验证逻辑
        return true;
    }

    /**
     * 从JWT令牌解析用户信息
     */
    private LoginVo parseUserInfoFromJwt(String jwtToken) {
        try {
            Claims claims = jwtUtil.parseToken(jwtToken);
            if (ObjectUtil.isEmpty(claims) || ObjectUtil.isEmpty(claims.get("currentUser"))) {
                return null;
            }
            return JSONObject.parseObject(claims.get("currentUser").toString(), LoginVo.class);
        } catch (Exception e) {
            log.error("解析JWT令牌失败", e);
            return null;
        }
    }

    /**
     * 验证用户令牌是否一致（防止用户被踢下线）
     */
    private boolean isUserTokenConsistent(String userToken, LoginVo loginVo) {
        String currentUserToken = stringRedisTemplate.opsForValue()
                .get(UserConstant.USER_TOKEN + loginVo.getUsername());
        return userToken.equals(currentUserToken);
    }

    /**
     * 当令牌即将过期时进行续期
     */
    private void renewTokenIfNecessary(String userToken, String oldJwtToken, LoginVo loginVo) {
        String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
        Long remainTimeToLive = stringRedisTemplate.getExpire(jwtTokenKey, TimeUnit.SECONDS);

        // 检查剩余时间是否需要续期（有效且小于等于10分钟）
        if (remainTimeToLive != null && remainTimeToLive > 0 && remainTimeToLive <= 600) {
            try {
                // 生成新的JWT令牌
                Claims oldClaims = jwtUtil.parseToken(oldJwtToken);
                Map<String, Object> newClaims = Map.of("currentUser", oldClaims.get("currentUser"));
                String newJwtToken = jwtUtil.generateToken(newClaims);

                // 计算过期时间（秒）
                long ttl = jwtProperties.getExpireTime() / 1000;

                // 更新Redis中的令牌
                stringRedisTemplate.opsForValue().set(jwtTokenKey, newJwtToken, ttl, TimeUnit.SECONDS);
                stringRedisTemplate.opsForValue()
                        .set(UserConstant.USER_TOKEN + loginVo.getUsername(), userToken, ttl, TimeUnit.SECONDS);

                log.debug("用户令牌已续期，用户名: {}", loginVo.getUsername());
            } catch (Exception e) {
                log.error("令牌续期失败", e);
                // 续期失败不影响当前请求，但应记录日志
            }
        }
    }

    /**
     * 验证用户是否有访问当前资源的权限
     */
    private boolean hasResourceAccessPermission(LoginVo loginVo, String requestMethodAndPath) {
        Set<String> resourcePaths = loginVo.getResourcePaths();
        if (resourcePaths == null || resourcePaths.isEmpty()) {
            return false;
        }

        // 使用AntPathMatcher匹配资源路径（支持通配符）
        return resourcePaths.stream()
                .anyMatch(resourcePath -> antPathMatcher.match(resourcePath, requestMethodAndPath));
    }
}
