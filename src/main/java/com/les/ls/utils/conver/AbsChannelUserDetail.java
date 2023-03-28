package com.les.ls.utils.conver;

import java.lang.annotation.*;

/**
 * @author ruoxiao.li
 * @date 2022/10/17
 * @desc 渠道用户详情表
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AbsChannelUserDetail {
    /**
     * 渠道id（不可重复,模块唯一标示）
     */
    int channelId();

    /**
     * 用户UserDetail实际Class
     */
    Class<?> userClass();
}
