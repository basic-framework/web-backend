//package com.zl.web.controller;
//
//import com.zl.common.result.Result;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 测试控制器
// * @Author GuihaoLv
// */
//@RestController
//@RequestMapping("/web/")
//@Slf4j
//@Tag(name = "日志测试接口", description = "用于测试不同级别的日志输出")
//public class LogTestController {
//
//    @GetMapping("/some-method")
//    @Operation(summary = "基础日志测试")
//    public void someMethod() {
//        log.info("这是一条信息日志");
//        log.error("这是一条错误日志");
//        log.debug("这是一条调试日志");
//    }
//
//    @GetMapping("/log-all-levels")
//    @Operation(summary = "所有级别日志测试")
//    public Result<Map<String, String>> testAllLogLevels() {
//        Map<String, String> messages = new HashMap<>();
//
//        // TRACE 级别 - 最详细的日志信息
//        log.trace("TRACE级别日志：最详细的调试信息，通常只在开发阶段使用");
//        messages.put("TRACE", "TRACE级别日志已输出");
//
//        // DEBUG 级别 - 调试信息
//        log.debug("DEBUG级别日志：调试信息，用于开发阶段问题排查");
//        messages.put("DEBUG", "DEBUG级别日志已输出");
//
//        // INFO 级别 - 一般信息
//        log.info("INFO级别日志：应用程序正常运行的信息");
//        messages.put("INFO", "INFO级别日志已输出");
//
//        // WARN 级别 - 警告信息
//        log.warn("WARN级别日志：警告信息，表示潜在问题但不影响正常运行");
//        messages.put("WARN", "WARN级别日志已输出");
//
//        // ERROR 级别 - 错误信息
//        log.error("ERROR级别日志：错误信息，表示系统出现了问题");
//        messages.put("ERROR", "ERROR级别日志已输出");
//
//        return Result.success(messages);
//    }
//
//    @GetMapping("/log-with-exception")
//    @Operation(summary = "异常日志测试")
//    public Result<String> testLogWithException() {
//        try {
//            // 模拟一个异常
//            int result = 10 / 0;
//        } catch (Exception e) {
//            // 记录异常日志，包含完整的堆栈信息
//            log.error("发生算术异常：除零错误", e);
//            return Result.success("异常日志已输出，请查看日志文件");
//        }
//        return Result.success("未发生异常");
//    }
//
//    @GetMapping("/log-with-params")
//    @Operation(summary = "带参数的日志测试")
//    public Result<String> testLogWithParams() {
//        String username = "testUser";
//        int userId = 12345;
//        String operation = "用户登录";
//
//        // 使用参数化日志，性能更好
//        log.info("用户操作：用户名={}, 用户ID={}, 操作={}", username, userId, operation);
//
//        // 使用格式化日志
//        log.debug("用户 {} (ID:{}) 执行了 {} 操作", username, userId, operation);
//
//        return Result.success("带参数的日志已输出");
//    }
//
//    @GetMapping("/log-performance-test")
//    @Operation(summary = "性能测试日志")
//    public Result<String> testPerformanceLog() {
//        long startTime = System.currentTimeMillis();
//
//        // 模拟一些业务操作
//        try {
//            Thread.sleep(100); // 模拟耗时操作
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//
//        log.info("操作执行完成，耗时: {}ms", duration);
//
//        if (duration > 50) {
//            log.warn("操作耗时较长: {}ms，建议优化", duration);
//        }
//
//        return Result.success("性能测试日志已输出，耗时: " + duration + "ms");
//    }
//}
