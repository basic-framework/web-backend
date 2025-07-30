package com.zl.web.manager.security;
import com.zl.common.properties.JwtProperties;
import com.zl.common.utils.authUtils.JwtUtil;
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

import java.util.function.Supplier;


/**
 * 授权管理器
 * @Author GuihaoLv
 */
@Component
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class JwtAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private JwtUtil jwtUtil;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        return new AuthorizationDecision(authentication.isAuthenticated());
    }

//    @Override
//    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
//        String method = requestAuthorizationContext.getRequest().getMethod();
//        String requestURL = requestAuthorizationContext.getRequest().getRequestURI();
//        String targetURL = method + requestURL;
//        String userToken = requestAuthorizationContext.getRequest().getHeader(UserConstant.USER_TOKEN);
//
//        if (ObjectUtil.isEmpty(userToken)) {
//            return new AuthorizationDecision(false);
//        }
//
//        String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
//        String jwtToken = stringRedisTemplate.opsForValue().get(jwtTokenKey);
//        if (ObjectUtil.isEmpty(jwtToken)) {
//            return new AuthorizationDecision(false);
//        }
//
//        Claims claims = jwtUtil.parseToken(jwtToken);
//        if (ObjectUtil.isEmpty(claims)) {
//            return new AuthorizationDecision(false);
//        }
//
//        LoginVo webUserLoginVo = JSONObject.parseObject(claims.get("currentUser").toString(), LoginVo.class);
//        String currentUserToken = stringRedisTemplate.opsForValue().get(UserConstant.USER_TOKEN + webUserLoginVo.getUsername());
//        if (!userToken.equals(currentUserToken)) {
//            return new AuthorizationDecision(false);
//        }
//
//        Long remainTimeToLive = stringRedisTemplate.opsForValue().getOperations().getExpire(jwtTokenKey);
//        if (remainTimeToLive <= 600) {
//            Map<String, Object> newClaims = new HashMap<>();
//            String userJsonString = String.valueOf(claims.get("currentUser"));
//            newClaims.put("currentUser", userJsonString);
//
//            String newJwtToken = jwtUtil.generateToken(newClaims);
//            long ttl = jwtProperties.getExpireTime() / 1000;
//
//            stringRedisTemplate.opsForValue().set(jwtTokenKey, newJwtToken, ttl, TimeUnit.SECONDS);
//            stringRedisTemplate.opsForValue().set(UserConstant.USER_TOKEN + webUserLoginVo.getUsername(), userToken, ttl, TimeUnit.SECONDS);
//        }
//
//        return new AuthorizationDecision(true);
//    }



}