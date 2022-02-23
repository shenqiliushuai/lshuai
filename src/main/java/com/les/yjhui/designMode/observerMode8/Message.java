package com.les.yjhui.designMode.observerMode8;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 消息 JDK8之前的实现
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019/11/24 17:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String message;
    private String from;
    private String to;
}
