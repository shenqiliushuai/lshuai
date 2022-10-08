package com.les.shengkai.pojo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class BankExcelModule implements Serializable {
    @Excel(name = "序号（必填）", fixedIndex = 0)
    private String no;

    @Excel(name = "账号（必填）", fixedIndex = 1)
    private String account;

    @Excel(name = "户名（必填）", fixedIndex = 2)
    private String huming;

    @Excel(name = "金额（必填）", fixedIndex = 3, type = 10)
    private Double amount;

    @Excel(name = "跨行标识（选填建行填0他行填1）", fixedIndex = 4)
    private String type;

    @Excel(name = "行名（跨行业务与联行行号不能同时为空）", fixedIndex = 5)
    private String bankName;

    @Excel(name = "联行行号（跨行业务与行名不能同时为空）", fixedIndex = 6)
    private String bankNumber;

    @Excel(name = "单位名称（必填显示在收款账户流水明细中）", fixedIndex = 7)
    private String content;

    @Excel(name = "备注（选填）", fixedIndex = 8)
    private String desc;
}
