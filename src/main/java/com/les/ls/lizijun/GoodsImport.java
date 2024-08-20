package com.les.ls.lizijun;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Data
public class GoodsImport implements Serializable {
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

    @Excel(name = "毛利润")
    private BigDecimal grossProfit;

    @Excel(name = "毛利率")
    private String grossProfitRate;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsImport goodsImport = (GoodsImport) o;
        return Objects.equals(batchNumber, goodsImport.batchNumber) &&
                Objects.equals(goodsName, goodsImport.goodsName) &&
                Objects.equals(aliasName, goodsImport.aliasName) &&
                Objects.equals(models, goodsImport.models) &&
                Objects.equals(manufacturer, goodsImport.manufacturer) &&
                Objects.equals(supplyPrice.multiply(new BigDecimal("100")),
                        goodsImport.supplyPrice.multiply(new BigDecimal("100")));
    }

    public int hashCode() {
        return Objects.hash(batchNumber, goodsName, aliasName, models, manufacturer, supplyPrice);
    }
}
