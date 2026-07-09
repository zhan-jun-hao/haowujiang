package com.haowujiang.sanguosha.interfaces.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.haowujiang.sanguosha.domain.exception.BusinessException;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusiness(BusinessException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(Result.fail(exception.getHttpStatus().value(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("request validation failed");
        return ResponseEntity.badRequest().body(Result.fail(400, message));
    }

    /**
     * Jackson 反序列化失败（如枚举值不合法、JSON 格式错误等）。
     * 前端传了非法的 action 值时，返回友好的中文错误提示。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Void>> handleMessageNotReadable(HttpMessageNotReadableException exception) {
        String message = "请求参数格式错误";
        Throwable cause = exception.getCause();
        if (cause instanceof InvalidFormatException formatException) {
            String fieldName = formatException.getPath().stream()
                    .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "")
                    .filter(s -> !s.isEmpty())
                    .reduce((a, b) -> b)
                    .orElse("action");
            Class<?> targetType = formatException.getTargetType();
            if (targetType != null && targetType.isEnum()) {
                Object[] enumConstants = targetType.getEnumConstants();
                message = "不支持的操作类型: " + formatException.getValue()
                        + "，支持: " + java.util.Arrays.stream(enumConstants)
                        .map(Object::toString)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
            } else {
                message = fieldName + " 的值格式不正确: " + formatException.getValue();
            }
        }
        return ResponseEntity.badRequest().body(Result.fail(400, message));
    }
}
