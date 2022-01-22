package com.les.ls.pojo.vo;

import com.les.ls.pojo.dto.WebResultEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * web响应VO对象
 *
 * @author lshuai
 */
@Getter
@Setter
@ToString
public class BaseWebResultVO {

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private Object data;

    public BaseWebResultVO(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseWebResultVO(WebResultEnum resultEnum, Object data) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMessage();
        this.data = data;
    }
}
