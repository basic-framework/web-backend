package com.zl.springai.functionCalling;


import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;


/**
 * FunctionCalling工具类
 * @Author GuihaoLv
 */
public class FCTools {

    /**
     * 简单的问候工具函数
     * @param name 用户名
     * @return 问候语
     */
    @Tool(description = "向指定用户发送问候语")
    public String sayHello(
            @ToolParam(description = "要问候的用户名") String name) {
        return "你好，" + name + "！很高兴见到你。";
    }

    /**
     * 简单的计算工具函数
     * @param a 第一个数字
     * @param b 第二个数字
     * @return 两数之和
     */
    @Tool(description = "计算两个数字的和")
    public String addNumbers(
            @ToolParam(description = "第一个数字") Double a,
            @ToolParam(description = "第二个数字") Double b) {
        Double result = a + b;
        return a + " + " + b + " = " + result;
    }

    /**
     * 简单的字符串处理工具函数
     * @param text 原始文本
     * @param operation 操作类型（upper/lower/reverse）
     * @return 处理后的文本
     */
    @Tool(description = "对文本进行简单处理")
    public String processText(
            @ToolParam(description = "要处理的原始文本") String text,
            @ToolParam(description = "操作类型：upper(转大写)、lower(转小写)、reverse(反转)") String operation) {
        switch (operation.toLowerCase()) {
            case "upper":
                return text.toUpperCase();
            case "lower":
                return text.toLowerCase();
            case "reverse":
                return new StringBuilder(text).reverse().toString();
            default:
                return "不支持的操作类型: " + operation;
        }
    }
}
