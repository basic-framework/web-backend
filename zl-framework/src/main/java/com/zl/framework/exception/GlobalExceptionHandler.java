package com.zl.framework.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.zl.common.result.Result;
import com.zl.framework.exception.base.BaseServiceException;
import com.zl.framework.exception.base.ErrorCode;
import com.zl.framework.exception.service.GlobalException;
import com.zl.framework.exception.service.ServiceException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * @Auther GuihaoLv
 */
@RestControllerAdvice
@Hidden
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理自定义业务异常
     *
     * @param exception 业务异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(BaseServiceException.class)
    public ResponseEntity<Result<Object>> handleBaseServiceException(BaseServiceException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.ok(Result.fail(exception.getCode(), exception.getMessage()));
    }


    /**
     * 处理旧的ServiceException（兼容性处理）
     *
     * @param exception 旧的ServiceException
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Result<Object>> handleServiceException(ServiceException exception, HttpServletRequest request) {
        logException(exception, request);
        Integer code = exception.getCode() != null ? exception.getCode() : ErrorCode.BUSINESS_ERROR.getCode();
        return ResponseEntity.ok(Result.fail(code, exception.getMessage()));
    }

    /**
     * 处理旧的GlobalException（兼容性处理）
     *
     * @param exception 旧的GlobalException
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Result<Object>> handleGlobalException(GlobalException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.ok(Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), exception.getMessage()));
    }

    /**
     * 处理参数校验异常（@RequestBody参数校验）
     *
     * @param exception 参数校验异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        logException(exception, request);
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), message));
    }

    /**
     * 处理参数校验异常（表单参数校验）
     *
     * @param exception 参数校验异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Object>> handleBindException(BindException exception, HttpServletRequest request) {
        logException(exception, request);
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), message));
    }

    /**
     * 处理参数校验异常（@RequestParam/@PathVariable参数校验）
     *
     * @param exception 参数校验异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Object>> handleConstraintViolationException(ConstraintViolationException exception, HttpServletRequest request) {
        logException(exception, request);
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), message));
    }

    /**
     * 处理缺少请求参数异常
     *
     * @param exception 缺少请求参数异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException exception, HttpServletRequest request) {
        logException(exception, request);
        String message = String.format("缺少必要参数: %s", exception.getParameterName());
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_MISSING.getCode(), message));
    }

    /**
     * 处理参数类型转换异常
     *
     * @param exception 参数类型转换异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        logException(exception, request);
        String message = String.format("参数类型错误: %s，期望类型: %s", exception.getName(), exception.getRequiredType().getSimpleName());
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_FORMAT_ERROR.getCode(), message));
    }

    /**
     * 处理非法参数异常
     *
     * @param exception 非法参数异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Object>> handleIllegalArgumentException(IllegalArgumentException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.PARAM_ERROR.getCode(), exception.getMessage()));
    }

    /**
     * 处理请求方法不支持异常
     *
     * @param exception 请求方法不支持异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Result<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception, HttpServletRequest request) {
        logException(exception, request);
        String message = String.format("请求方法不支持: %s", exception.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Result.fail(ErrorCode.REQUEST_METHOD_ERROR.getCode(), message));
    }

    /**
     * 处理404异常
     *
     * @param exception 404异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Result<Object>> handleNoHandlerFoundException(NoHandlerFoundException exception, HttpServletRequest request) {
        logException(exception, request);
        String message = String.format("请求路径不存在: %s", exception.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.fail(ErrorCode.DATA_NOT_FOUND.getCode(), message));
    }

    /**
     * 处理文件上传超过最大限制异常
     *
     * @param exception 文件上传异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.badRequest().body(Result.fail(ErrorCode.FILE_SIZE_EXCEED.getCode(), "上传文件大小超过限制"));
    }

    /**
     * 处理文件未找到异常
     *
     * @param exception 文件未找到异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Result<Object>> handleFileNotFoundException(FileNotFoundException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Result.fail(ErrorCode.FILE_NOT_FOUND.getCode(), exception.getMessage()));
    }

    /**
     * 处理没有权限访问接口异常
     *
     * @param exception 权限访问异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<Object>> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ErrorCode.FORBIDDEN.getCode(), "没有权限访问接口"));
    }

    /**
     * 处理运行时异常
     *
     * @param exception 运行时异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Result<Object>> handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        logException(exception, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), exception.getMessage()));
    }

    /**
     * 处理其他未知异常
     *
     * @param exception 未知异常
     * @param request 请求对象
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleUnknownException(Exception exception, HttpServletRequest request) {
        logException(exception, request);
        String message = ExceptionUtil.stacktraceToString(exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), message));
    }

    /**
     * 记录异常日志
     *
     * @param exception 异常对象
     * @param request 请求对象
     */
    private void logException(Exception exception, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String params = request.getQueryString();
        
        log.error("请求异常 - URI: {} {}, 参数: {}, 异常: {}", method, uri, params, exception.getMessage(), exception);
    }

    /**
     * 将String类型的错误码转换为Integer类型（兼容性处理）
     *
     * @param code String类型的错误码
     * @return Integer类型的错误码
     */
    private Integer convertStringCodeToInteger(String code) {
        if (code == null) {
            return ErrorCode.SYSTEM_ERROR.getCode();
        }
        try {
            return Integer.parseInt(code);
        } catch (NumberFormatException e) {
            return ErrorCode.SYSTEM_ERROR.getCode();
        }
    }
}