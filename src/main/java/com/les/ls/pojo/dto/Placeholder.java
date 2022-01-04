package com.les.ls.pojo.dto;

import lombok.Data;

/**
 * 占位符对象
 *
 * @author levis
 */
@Data
public class Placeholder {
    /**
     * 机构编码
     */
    private String orgCode;
    /**
     * 日期  格式：yyyy-MM-dd
     */
    private String date;
    /**
     * 日期  格式：yyyyMMdd
     */
    private String yyyyMMdd;
    /**
     * 时间  格式：HH:mm:ss
     */
    private String time;
    /**
     * 时间  格式：HHmmss
     */
    private String HHmmss;

    /**
     * 主程序调用
     * 写日期函数时将日期字段-格式不同的属性全部赋值
     *
     * @param date 日期 格式 yyyy-MM-dd
     */
    public void setDate(String date) {
        this.date = date;
        if (date != null) {
            this.setYyyyMMdd(date.replaceAll("-", ""));
        }
    }
}
