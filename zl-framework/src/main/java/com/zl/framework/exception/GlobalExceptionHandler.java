package com.zl.framework.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 * @Auther GuihaoLv
 */
@RestControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理运行时异常。
     * 返回HTTP响应状态码500，包含错误代码和错误消息。
     *
     * @param exception 运行时异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("其他未知异常 -> ", exception);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MapUtil.<String, Object>builder()
                        .put("code", HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .put("msg", exception.getMessage())
                        .build());
    }




    /**
     * 处理IllegalArgumentException。
     * 返回默认的错误代码400和对应的错误消息。
     *
     * @param exception 非法参数异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("非法参数异常处理 -> ", exception);
        }
        // 使用HTTP状态码400 Bad Request表示客户端请求的语法不对
        return ResponseEntity.badRequest().body(MapUtil.<String, Object>builder()
                .put("code", 400) // 或者定义一个更具体的错误代码
                .put("msg", exception.getMessage())
                .build());
    }

    /**
     * 处理自定义异常BaseException。
     * 返回自定义异常中的错误代码和错误消息。
     *
     * @param exception 自定义异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("自定义异常处理 -> ", exception);
        }
        return ResponseEntity.ok(MapUtil.<String, Object>builder()
                .put("code", exception.getCode())
                .put("msg", exception.getDefaultMessage())
                .build());
    }

    /**
     * 处理文件上传超过最大限制异常。
     * 返回HTTP响应状态码500，包含错误代码和错误消息。
     *
     * @param exception 文件上传异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("文件上传超过最大限制异常 -> ", exception);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MapUtil.<String, Object>builder()
                        .put("code", HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .put("msg", "上传图片大小不能超过5M，格式需为jpg、png、gif")
                        .build());
    }

    /**
     * 处理其他未知异常。
     * 返回HTTP响应状态码500，包含错误代码和异常堆栈信息。
     *
     * @param exception 未知异常
     * @return 响应数据，包含错误代码和异常堆栈信息
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("其他未知异常 -> ", exception);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MapUtil.<String, Object>builder()
                        .put("code", HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .put("msg", ExceptionUtil.stacktraceToString(exception))
                        .build());
    }

    /**
     * 处理FileNotFoundException异常。
     * 返回HTTP响应状态码400，包含错误代码和错误消息。
     *
     * @param exception 文件未找到异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Object> handleFileNotFoundException(FileNotFoundException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("文件不存在 -> ", exception);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MapUtil.<String, Object>builder()
                        .put("code", HttpStatus.BAD_REQUEST.value())
                        .put("msg", exception.getMessage())
                        .build());
    }

    /**
     * 处理没有权限访问接口异常。
     * 返回HTTP响应状态码401，包含错误代码和错误消息。
     *
     * @param exception 权限访问异常
     * @return 响应数据，包含错误代码和错误消息
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception) {
        exception.printStackTrace();
        if (ObjectUtil.isNotEmpty(exception.getCause())) {
            log.error("没有权限访问接口异常 -> ", exception);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(MapUtil.<String, Object>builder()
                        .put("code", HttpStatus.UNAUTHORIZED.value())
                        .put("msg", "没有权限访问接口")
                        .build());
    }



    /**
     * 处理key重复异常。
     * 返回HTTP响应状态码200，包含错误代码和错误消息。
     *
     * @param exception key重复异常
     * @return 响应数据，包含错误代码和错误消息
     */
//    @ExceptionHandler(DuplicateKeyException.class)
//    public ResponseEntity<Object> handleDuplicateKeyException(DuplicateKeyException exception) {
//        exception.printStackTrace();
//        if (ObjectUtil.isNotEmpty(exception.getCause())) {
//            log.error("其他未知异常 -> ", exception);
//        }
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(MapUtil.<String, Object>builder()
//                        .put("code", HttpStatus.INTERNAL_SERVER_ERROR.value())
//                        .put("msg", "操作失败，数据重复")
//                        .build());
//    }



}