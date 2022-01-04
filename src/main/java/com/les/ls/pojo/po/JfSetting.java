package com.les.ls.pojo.po;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 配置表对象
 *
 * @author levis
 */
@Data
//@TableName(autoResultMap = true)
public class JfSetting implements Serializable {

    /**
     * 注解ID
     */
    //@TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String channel;// 渠道名称
    private String orgCode;// 机构id
    private String downloadType;//ftp/email/http/oracle/mysql/mssql
    private String fileType;//xml/json/txt/excel/pdf
    private Integer tabType;//渠道/HIS:1渠道 2HIS 默认1
    private Integer state = 0;//1正常，0失效 默认0
    //@TableField(exist = false)
    private String orgName;
    //@TableField(exist = false)
    private String channelName;
    //@TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date createTime;
    //@TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject downloadConfig;//下载设置配置
    //@TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject parseConfig;//解析设置配置
    //@TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject mappingConfig;//映射设置配置
    //@TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject refundConfig;//退款设置配置

}
