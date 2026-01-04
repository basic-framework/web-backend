package com.zl.framework.manager.security;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.zl.common.context.UserThreadLocal;
import com.zl.common.properties.JwtProperties;
import com.zl.common.utils.authUtils.JwtUtil;
import com.zl.model.vo.LoginVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Set;
import java.util.function.Supplier;

/**
 * 授权管理器
 * @Author GuihaoLv
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication,
                                       RequestAuthorizationContext requestContext) {
        try {
            // 1. 提取请求信息
            String requestMethodAndPath = extractRequestInfo(requestContext);

            // 2. 从ThreadLocal获取用户信息（由UserTokenInterceptor已设置）
            LoginVo loginVo = getUserFromThreadLocal();
            if (loginVo == null) {
                log.warn("ThreadLocal中未找到用户信息，路径: {}", requestMethodAndPath);
                return new AuthorizationDecision(false);
            }

            // 3. 验证资源访问权限
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
     * 从ThreadLocal获取用户信息
     */
    private LoginVo getUserFromThreadLocal() {
        try {
            String userSubject = UserThreadLocal.getSubject();
            if (ObjectUtil.isEmpty(userSubject)) {
                return null;
            }
            return JSONObject.parseObject(userSubject, LoginVo.class);
        } catch (Exception e) {
            log.error("从ThreadLocal获取用户信息失败", e);
            return null;
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
