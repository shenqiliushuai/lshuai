package com.les.ls.lizijun;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ExcelProcess {
    public static void main(String[] args) {
        ImportParams params = new ImportParams();
        params.setHeadRows(1);
        params.setSheetNum(1);
        List<GoodsImport> goodsImportList = ExcelImportUtil.importExcel(new File("C:\\Users\\HP\\Desktop\\云药房药品目录20240805.xlsx"), GoodsImport.class, params);
        log.info("已导入{}条数据", goodsImportList.size());

        // 使用 HashSet 去重
        Set<GoodsImport> uniqueGoodsList = new HashSet<>(goodsImportList);
        // 输出去重后的集合
        log.info("去重后的集合大小->{}", uniqueGoodsList.size());

        List<GoodsExport> goodsExports = new ArrayList<>();
        for (GoodsImport goodsImport : uniqueGoodsList) {
            GoodsExport goodsExport = new GoodsExport();
            BeanUtil.copyProperties(goodsImport, goodsExport);

            // 毛利润
            BigDecimal grossProfit = goodsExport.getSalesPrice().subtract(goodsExport.getSupplyPrice());
            goodsExport.setGrossProfit(grossProfit);
            // 毛利率 四舍五入模式
            BigDecimal grossProfitRate = grossProfit.divide(goodsExport.getSalesPrice(), 4, RoundingMode.HALF_UP);
            goodsExport.setGrossProfitRate(grossProfitRate.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) + "%");

            // 一折
            BigDecimal off_10 = goodsExport.getSalesPrice().multiply(new BigDecimal("0.1"));
            goodsExport.setOff_10(off_10);
            // 一折毛利润
            BigDecimal grossProfit_10 = off_10.subtract(goodsExport.getSupplyPrice());
            goodsExport.setGrossProfit_10(grossProfit_10);
            // 一折毛利率 四舍五入模式
            BigDecimal grossProfitRateOff_10 = grossProfit_10.divide(goodsExport.getSalesPrice(), 4, RoundingMode.HALF_UP);
            goodsExport.setGrossProfitRateOff_10(grossProfitRateOff_10.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) + "%");

            // 三折
            BigDecimal off_30 = goodsExport.getSalesPrice().multiply(new BigDecimal("0.3"));
            goodsExport.setOff_30(off_30);
            // 三折毛利润
            BigDecimal grossProfit_30 = off_30.subtract(goodsExport.getSupplyPrice());
            goodsExport.setGrossProfit_30(grossProfit_30);
            // 三折毛利率 四舍五入模式
            BigDecimal grossProfitRateOff_30 = grossProfit_30.divide(goodsExport.getSalesPrice(), 4, RoundingMode.HALF_UP);
            goodsExport.setGrossProfitRateOff_30(grossProfitRateOff_30.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) + "%");

            // 七折
            BigDecimal off_70 = goodsExport.getSalesPrice().multiply(new BigDecimal("0.7"));
            goodsExport.setOff_70(off_70);
            // 七折毛利润
            BigDecimal grossProfit_70 = off_70.subtract(goodsExport.getSupplyPrice());
            goodsExport.setGrossProfit_70(grossProfit_70);
            // 七折毛利率 四舍五入模式
            BigDecimal grossProfitRateOff_70 = grossProfit_70.divide(goodsExport.getSalesPrice(), 4, RoundingMode.HALF_UP);
            goodsExport.setGrossProfitRateOff_70(grossProfitRateOff_70.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP) + "%");

            goodsExports.add(goodsExport);
        }
        log.info("开始输出...");
        try (FileOutputStream fos = new FileOutputStream("C:\\Users\\HP\\Desktop\\云药房药品目录20240805_result.xlsx")) {
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, null), GoodsExport.class, goodsExports);
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            log.error("导出异常！msg->{}", e.getMessage());
        }
        log.info("结束...");
    }
}
