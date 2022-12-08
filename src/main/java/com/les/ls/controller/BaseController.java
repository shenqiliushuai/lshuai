package com.les.ls.controller;

import com.les.ls.pojo.dto.WebResultEnum;
import com.les.ls.pojo.vo.BaseWebResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * web请求基础功能类
 *
 * @author lshuai
 */
@Slf4j
@ControllerAdvice
@CrossOrigin
public class BaseController {

    /**
     * 统一异常处理
     *
     * @param exception 异常信息
     * @return 统一响应体
     */
    @ExceptionHandler(Exception.class)
    public BaseWebResultVO customException(Exception exception) {
        log.error("发生了一个未被捕获的异常，异常信息->", exception);
        return new BaseWebResultVO(WebResultEnum.SERVER_ERROR, null);
    }
}
