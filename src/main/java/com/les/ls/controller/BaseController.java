package com.les.ls.controller;

import com.les.ls.exception.TokenException;
import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

/**
 * web请求基础功能类
 *
 * @author lshuai
 */
@Slf4j
@ControllerAdvice
public class BaseController {

    @ExceptionHandler(BindException.class)
    public BaseWebResultVO bindException(BindException exception) {
        log.error("参数绑定异常！", exception);
        Optional<FieldError> fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();
        return fieldError.map(error -> new BaseWebResultVO(WebResultEnum.FAILED, error.getDefaultMessage())).orElseGet(
                () -> new BaseWebResultVO(WebResultEnum.FAILED, null));

    }

    @ExceptionHandler(TokenException.class)
    public BaseWebResultVO TokenException(TokenException exception) {
        return new BaseWebResultVO(WebResultEnum.FAILED, exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseWebResultVO RuntimeException(Exception exception) {
        log.error("发生了一个未被捕获的异常！", exception);
        return new BaseWebResultVO(WebResultEnum.SERVER_ERROR, null);
    }
}
