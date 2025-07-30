package com.zl.common.utils;


 /**
 * 搜索关键字处理工具类
 * @Author GuihaoLv
 */
public class SearchUtils {
    /**
     * 处理搜索关键词，添加通配符
     * @param keyword 原始关键词
     * @return 处理后的关键词
     */
    public static String processKeyword(String keyword) {
        // 去除前后空格
        keyword = keyword.trim();

        // 对于长度大于2的关键词，添加通配符以支持模糊搜索
        if (keyword.length() > 2) {
            keyword = "*" + keyword + "*";
        }

        // 转义特殊字符
        keyword = keyword.replace("+", "\\+")
                .replace("-", "\\-")
                .replace(">", "\\>")
                .replace("<", "\\<")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("*", "\\*")
                .replace("\"", "\\\"")
                .replace("=", "\\=");

        return keyword;
    }


}