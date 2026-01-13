package com.zl.framework.interceptor;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zl.common.constant.UserConstant;
import com.zl.common.context.UserThreadLocal;
import com.zl.common.utils.authUtils.JwtUtil;
import io.jsonwebtoken.Claims; // 新增：兼容过滤器的Claims类型
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 拦截用户信息存ThreadLocal中的拦截器
 * @Auther: GuihaoLv
 */
@Component
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 非控制器请求，直接放行
        if (!(handler instanceof HandlerMethod)) {
            log.info("【拦截器】非控制器请求，放行：{}", request.getRequestURI());
            return true;
        }

        String requestUri = request.getRequestURI();
        log.info("【拦截器】开始处理请求：{}", requestUri);

        // 1. 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        log.info("【拦截器】获取到Authorization头：{}", authHeader);

        if (StrUtil.isEmpty(authHeader)) {
            log.warn("【拦截器】请求{}缺少Authorization请求头", requestUri);
            return true;
        }

        // 2. 处理token（兼容Bearer前缀）
        String userToken = authHeader.replaceFirst("^Bearer\\s+", "");
        log.info("【拦截器】处理后的userToken：{}", userToken);

        try {
            // 3. 查Redis
            String jwtTokenKey = UserConstant.JWT_TOKEN + userToken;
            String jwtToken = stringRedisTemplate.opsForValue().get(jwtTokenKey);
            log.info("【拦截器】Redis查询结果 - key：{}，jwtToken：{}", jwtTokenKey, jwtToken);

            if (StrUtil.isEmpty(jwtToken)) {
                log.warn("【拦截器】Redis中无该token，key：{}", jwtTokenKey);
                return true;
            }

            // 4. 解析JWT（修改：用Claims接收，兼容过滤器的返回类型）
            Claims claims = jwtUtil.parseToken(jwtToken); // 仅改这行：Map → Claims
            log.info("【拦截器】JWT解析结果：{}", claims);

            Object userObj = claims.get("currentUser");
            log.info("【拦截器】JWT中currentUser字段值：{}", userObj);

            if (ObjectUtil.isEmpty(userObj)) {
                log.warn("【拦截器】JWT中无currentUser字段");
                return true;
            }

            String currentUser = String.valueOf(userObj);
            if (StrUtil.isEmpty(currentUser)) {
                log.warn("【拦截器】currentUser字段为空字符串");
                return true;
            }

            // 5. 存入ThreadLocal
            UserThreadLocal.setSubject(currentUser);
            log.info("【拦截器】成功存入ThreadLocal，用户信息：{}", currentUser);

        } catch (Exception e) {
            log.error("【拦截器】处理token时抛出异常", e);
        }

        return true;
    }

    // 新增：请求完成后清理ThreadLocal，避免内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            UserThreadLocal.remove();
            log.info("【拦截器】清理ThreadLocal，请求URI：{}", request.getRequestURI());
        } catch (Exception e) {
            log.error("【拦截器】清理ThreadLocal失败", e);
        }
    }
}