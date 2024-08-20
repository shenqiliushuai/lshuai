package com.les.ls.lizijun;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsExport {
    @Excel(name = "ID")
    private String id;

    @Excel(name = "批准文号")
    private String batchNumber;

    @Excel(name = "商品名")
    private String goodsName;

    @Excel(name = "通用名")
    private String aliasName;

    @Excel(name = "规格")
    private String models;

    @Excel(name = "生产厂家")
    private String manufacturer;

    @Excel(name = "供货价")
    private BigDecimal supplyPrice;

    @Excel(name = "销售价")
    private BigDecimal salesPrice;

    @Excel(name = "毛利润（销售价格）")
    private BigDecimal grossProfit;

    @Excel(name = "毛利润率（销售价格）")
    private String grossProfitRate;

    @Excel(name = "一折")
    private BigDecimal off_10;

    @Excel(name = "毛利润（一折）")
    private BigDecimal grossProfit_10;

    @Excel(name = "毛利润率（一折）")
    private String grossProfitRateOff_10;

    @Excel(name = "三折")
    private BigDecimal off_30;

    @Excel(name = "毛利润（三折）")
    private BigDecimal grossProfit_30;

    @Excel(name = "毛利润率（三折）")
    private String grossProfitRateOff_30;

    @Excel(name = "七折")
    private BigDecimal off_70;

    @Excel(name = "毛利润（七折）")
    private BigDecimal grossProfit_70;

    @Excel(name = "毛利润率（七折）")
    private String grossProfitRateOff_70;
}
