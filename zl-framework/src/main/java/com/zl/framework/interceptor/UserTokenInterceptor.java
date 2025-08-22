package com.zl.framework.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.zl.common.constant.UserConstant;
import com.zl.common.context.UserThreadLocal;
import com.zl.common.properties.JwtProperties;
import com.zl.common.utils.authUtils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
* 拦截用户信息存ThreadLocal中的拦截器
* @Auther: GuihaoLv
*/
@Component
public class UserTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JwtUtil jwtUtil;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //非控制器请求，非RequestMapping
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        //从头部中拿userToken
        String userToken=request.getHeader("Authorization");
        if(!ObjectUtil.isEmpty(userToken)){
            String jwtTokenKey=UserConstant.JWT_TOKEN+userToken;
            String jwtToken=stringRedisTemplate.opsForValue().get(jwtTokenKey);
            //拿到jwt令牌不为空
            if(!ObjectUtil.isEmpty(jwtToken)){
                //解析jwt令牌
                Map<String, Object> claims= jwtUtil.parseToken(jwtToken);
                Object userObj=claims.get("currentUser");
                String currentUser=String.valueOf(userObj);
                //将用户信息存入ThreadLocal
                UserThreadLocal.setSubject(currentUser);
            }
        }
        return true;

    }




    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //移除当前线程变量中的数据
        UserThreadLocal.remove();
    }


}