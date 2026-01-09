package com.zl.springai.controller;

import com.zl.common.result.Result;
import com.zl.springai.functionCalling.FCTools;
import com.zl.springai.prompt.ChatConstant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * FunctionCalling测试控制器
 * @Author GuihaoLv
 */
@RestController
@RequestMapping("/api/function-calling")
public class FunctionCallingController {

    @Autowired
    private ChatClient functionCallingClient;

    @Autowired
    private FCTools fcTools;

    /**
     * FunctionCalling功能测试接口
     * @param message 用户输入的消息
     * @return AI响应结果
     */
    @PostMapping("/test")
    public Result<String> testFunctionCalling(@RequestBody String message) {
        try {
            String response = functionCallingClient.prompt()
                    .user(message)
                    .call()
                    .content();
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.fail("FunctionCalling测试失败: " + e.getMessage());
        }
    }

    /**
     * 直接调用工具函数测试
     * @param toolName 工具名称
     * @param params 参数
     * @return 工具执行结果
     */
    @PostMapping("/direct-call")
    public Result<String> directCallTool(
            @RequestParam String toolName,
            @RequestParam(required = false) String params) {
        try {
            String result;
            switch (toolName) {
                case "sayHello":
                    result = fcTools.sayHello(params != null ? params : "用户");
                    break;
                case "addNumbers":
                    // 简单解析参数，格式如 "3,5"
                    if (params != null && params.contains(",")) {
                        String[] numbers = params.split(",");
                        double a = Double.parseDouble(numbers[0].trim());
                        double b = Double.parseDouble(numbers[1].trim());
                        result = fcTools.addNumbers(a, b);
                    } else {
                        result = "addNumbers工具需要两个数字参数，格式如：3,5";
                    }
                    break;
                case "processText":
                    // 简单解析参数，格式如 "hello,upper"
                    if (params != null && params.contains(",")) {
                        String[] parts = params.split(",", 2);
                        result = fcTools.processText(parts[0].trim(), parts[1].trim());
                    } else {
                        result = "processText工具需要文本和操作类型参数，格式如：hello,upper";
                    }
                    break;
                default:
                    result = "不支持的工具名称: " + toolName;
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("工具调用失败: " + e.getMessage());
        }
    }
}