package com.les.shengkai.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShengkaiExcelImport implements Serializable {

    @Excel(name = "企业名称")
    private String name;

    @Excel(name = "联系电话")
    private String phone;

    @Excel(name = "工商年报电话")
    private String icPhone;

    @Excel(name = "更多联系电话")
    private String morePhone;
}
