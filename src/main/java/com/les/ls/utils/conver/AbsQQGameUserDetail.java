package com.les.ls.utils.conver;

import lombok.Data;

import java.util.Date;

@Data
public class AbsQQGameUserDetail {
    private Integer appId;
    private Integer channelId;
    private String thirdUserId;
    private Integer loginMethod;
    private String channelUserId;
    private Date createTime;
    private Date lastLoginTime;

    private String openId;
    private String openKey;
    private String pf;
    private String pfKey;
    private String packageId;
}
