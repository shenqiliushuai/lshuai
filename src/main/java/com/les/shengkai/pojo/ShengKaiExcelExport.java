package com.les.shengkai.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class ShengKaiExcelExport implements Serializable {
    @Excel(name = "名字")
    private String name;

    @Excel(name = "电话")
    private String phone;
}
