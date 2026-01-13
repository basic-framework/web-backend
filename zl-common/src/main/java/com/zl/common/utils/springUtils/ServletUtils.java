package com.zl.common.utils.springUtils;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * 客户端工具类
 * @Author GuihaoLv
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletUtils {

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取ServletRequestAttributes
     */
    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map 键：参数名，值：参数值数组拼接后的字符串
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        // 获取请求参数的原始Map（key: 参数名，value: 参数值数组）
        Map<String, String[]> paramArrayMap = getParams(request);

        // 遍历参数，将数组拼接为字符串
        for (Map.Entry<String, String[]> entry : paramArrayMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            // 使用Tomcat的StringUtils.join方法（参数：数组、分隔符）
            String paramValue = StringUtils.join(Arrays.toString(paramValues), ","); // 用逗号分隔，可根据需求修改
            params.put(paramName, paramValue);
        }
        return params;
    }
    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }


    /**
     * 获取客户端IP
     */
    public static String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}