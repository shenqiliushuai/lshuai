package com.les.ls.utils.conver;

import lombok.Data;

import java.util.Date;

/**
 * @author ruoxiao.li
 * @date 2022/10/17
 * @describe
 */
@Data
public class AbsUserDetail {
    private Long id;
    private Integer appId;
    private Integer channelId;
    private String thirdUserId;
    private Integer loginMethod;
    private String channelUserId;
    private String column0;
    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private Date createTime;
    private Date lastLoginTime;
}
