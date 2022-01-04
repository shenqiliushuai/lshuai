package com.les.ls.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 下载器出参
 *
 * @author levis
 */
@Data
public class DownloadResult implements Serializable {
    /**
     * 1:list有效
     * 2:fileList 文件物理路径有效
     * 3:xml/json String有效
     */
    private Integer type;
    /**
     * 数据库列及值
     */
    private List<Map<String, Object>> list;
    /**
     * 附件绝对路径
     */
    private List<String> fileList;
    /**
     * json/xml报文
     */
    private String data;

    public static final Integer TYPE_LIST = 1;
    public static final Integer TYPE_FILE_LIST = 2;
    public static final Integer TYPE_DATA = 3;
}
