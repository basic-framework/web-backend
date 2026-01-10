package com.zl.common.utils.jsonUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类（基于fastjson2）
 * @Author GuihaoLv
 */
public class JsonUtils {
    
    /**
     * 对象转Json字符串
     * @param object 对象
     * @return Json字符串
     */
    public static String toJsonString(Object object) {
        try {
            return JSON.toJSONString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
    
    /**
     * Json字符串转对象
     * @param jsonString Json字符串
     * @param clazz 对象类型
     * @return 对象
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseObject(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Json字符串转对象（支持泛型）
     * @param jsonString Json字符串
     * @param typeReference 类型引用
     * @return 对象
     */
    public static <T> T parseObject(String jsonString, TypeReference<T> typeReference) {
        try {
            return JSON.parseObject(jsonString, typeReference);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Json字符串转List
     * @param jsonString Json字符串
     * @param clazz 对象类型
     * @return List对象
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        try {
            return JSON.parseArray(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Json字符串转Map
     * @param jsonString Json字符串
     * @return Map对象
     */
    public static Map<String, Object> parseMap(String jsonString) {
        try {
            return JSON.parseObject(jsonString, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Json字符串转JSONObject
     * @param jsonString Json字符串
     * @return JSONObject对象
     */
    public static JSONObject parseJSONObject(String jsonString) {
        try {
            return JSON.parseObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
    
    /**
     * 判断是否为有效的JSON字符串
     * @param jsonString 字符串
     * @return 是否有效
     */
    public static boolean isValidJson(String jsonString) {
        try {
            JSON.parse(jsonString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 格式化JSON字符串
     * @param jsonString Json字符串
     * @return 格式化后的Json字符串
     */
    public static String formatJson(String jsonString) {
        try {
            Object object = JSON.parse(jsonString);
            return JSON.toJSONString(object, String.valueOf(true));
        } catch (Exception e) {
            e.printStackTrace();
            return jsonString;
        }
    }
}