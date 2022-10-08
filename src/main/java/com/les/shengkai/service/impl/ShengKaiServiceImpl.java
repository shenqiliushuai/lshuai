package com.les.shengkai.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.les.ls.utils.TerminalUtils;
import com.les.shengkai.config.ExcelConfig;
import com.les.shengkai.pojo.BankExcelModule;
import com.les.shengkai.pojo.ShengKaiExcelExport;
import com.les.shengkai.pojo.ShengkaiExcelImport;
import com.les.shengkai.service.ShengKaiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Slf4j
@Service
public class ShengKaiServiceImpl implements ShengKaiService {

    @Resource
    private ExcelConfig excelConfig;

    @Override
    public void doExcelParser() {
        String workDir = TerminalUtils.getWorkDir();
        //String workDir = "C:\\Users\\Administrator\\Desktop";
        File[] files = new File(workDir).listFiles();
        if (files == null) {
            log.warn("未检测到文件！路径->{}", workDir);
            return;
        }
        ImportParams params = new ImportParams();
        //设置标题的行数，有标题时一定要有
        params.setTitleRows(1);
        //设置表头的行数
        params.setHeadRows(1);
        List<ShengKaiExcelExport> exportList = new ArrayList<>();
        for (File file : files) {
            if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                List<ShengkaiExcelImport> importList = ExcelImportUtil.importExcel(file, ShengkaiExcelImport.class, params);
                for (ShengkaiExcelImport excelImport : importList) {
                    if (StringUtils.hasLength(excelImport.getPhone())) {
                        if (!excelImport.getPhone().contains("-") && excelImport.getPhone().length() == 11) {
                            ShengKaiExcelExport excelExport = new ShengKaiExcelExport();
                            excelExport.setName("Z" + excelImport.getName());
                            excelExport.setPhone(excelImport.getPhone());
                            exportList.add(excelExport);
                        }
                    }
                    String icPhone = excelImport.getIcPhone();
                    if (StringUtils.hasLength(icPhone)) {
                        String[] strs = icPhone.split(";");
                        for (String s : strs) {
                            String str = s.trim();
                            if (StringUtils.hasLength(str)) {
                                if (str.length() != 11 || str.contains("-") || (excelImport.getPhone() != null && excelImport.getPhone().equals(str)) || str.startsWith("0")) {
                                    continue;
                                }
                                ShengKaiExcelExport excelExport = new ShengKaiExcelExport();
                                excelExport.setName("Z" + excelImport.getName());
                                excelExport.setPhone(str);
                                exportList.add(excelExport);
                            }
                        }
                    }
                }
                log.info("文件{}共读取{}行...", file.getName(), importList.size());
            } else {
                log.warn("已跳过文件{}", file.getName());
            }
        }
        if (exportList.isEmpty()) {
            log.warn("{}目录无数据处理...", workDir);
            return;
        } else {
            log.info("共需要处理{}条数据...", exportList.size());
        }
        Set<ShengKaiExcelExport> excelExportSet = new TreeSet<>(Comparator.comparing(ShengKaiExcelExport::getPhone));
        excelExportSet.addAll(exportList);
        List<ShengKaiExcelExport> newExportList = new ArrayList<>(excelExportSet);
        log.info("去重后需要导出{}条数据...", newExportList.size());
        int fileSplitSize = 5000, index = 0;
        do {
            index++;
            List<ShengKaiExcelExport> splitList = newExportList.subList(0, Math.min(newExportList.size(), fileSplitSize));
            File saveFile = new File(workDir + File.separator + "export");
            if (!saveFile.exists()) {
                if (!saveFile.mkdirs()) {
                    log.warn("目录创建失败！dir->{}", saveFile.getPath());
                }
            }
            try (FileOutputStream fos = new FileOutputStream(saveFile + File.separator + index + ".xlsx")) {
                Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, null, index + ""),
                        ShengKaiExcelExport.class, splitList);
                workbook.write(fos);
                workbook.close();
            } catch (Exception e) {
                log.error("导出异常！msg->{}", e.getMessage());
            }
            log.info("文件{}导出成功！还剩余{}条数据...", index, newExportList.size());
        } while (newExportList.size() != 0);

    }

    @Override
    public void bankParser() {
        log.info("任务开始...");
        String workDir = TerminalUtils.getWorkDir() + File.separator + "bank";
        //String workDir = "C:\\Users\\Administrator\\Desktop";
        File[] files = new File(workDir).listFiles();
        if (files == null) {
            log.warn("未检测到文件！路径->{}", workDir);
            return;
        }

        ImportParams params = new ImportParams();
        //设置表头的行数
        params.setHeadRows(1);
        params.setSheetNum(1);
        params.setVerifyHandler(excelConfig);
        List<BankExcelModule> allImportList = new ArrayList<>();
        int j = 1;
        for (File file : files) {
            if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                List<BankExcelModule> importList = ExcelImportUtil.importExcel(file, BankExcelModule.class, params);
                BankExcelModule module = importList.get(0);
                if (StringUtils.hasLength(module.getContent())) {
                    for (BankExcelModule bankExcelImport : importList) {
                        bankExcelImport.setNo(j + "");
                        j++;
                        bankExcelImport.setHuming(StringUtils.replace(bankExcelImport.getHuming(), " ", ""));
                        if (bankExcelImport.getAmount() == null || bankExcelImport.getAmount() == 0) {
                            continue;
                        }

                        if (!StringUtils.hasLength(bankExcelImport.getType())
                                && !StringUtils.hasLength(bankExcelImport.getBankName())
                                && !StringUtils.hasLength(bankExcelImport.getBankNumber())) {
                            bankExcelImport.setType("0");
                            bankExcelImport.setBankName("建设银行南召支行");
                            bankExcelImport.setBankNumber("105513300017");
                            allImportList.add(bankExcelImport);
                            continue;
                        }

                        if (bankExcelImport.getType() != null) {
                            int type;
                            try {
                                type = Integer.parseInt(bankExcelImport.getType());
                            } catch (Exception e) {
                                log.warn("数值转换失败！type->{}", bankExcelImport.getType());
                                continue;
                            }
                            if (type == 0) {
                                bankExcelImport.setBankName("建设银行南召支行");
                                bankExcelImport.setBankNumber("105513300017");
                                allImportList.add(bankExcelImport);
                                continue;
                            }
                        }
                        allImportList.add(bankExcelImport);
                    }
                } else {
                    log.error("未读取到摘要！file->{}", file.getName());
                }
            } else {

                log.warn("已跳过文件{}", file.getName());
            }
        }
        log.info("共读取{}行，开始导出...", allImportList.size());

        File saveFile = new File(workDir + File.separator + "bank" + File.separator + "export");
        if (!saveFile.exists()) {
            if (!saveFile.mkdirs()) {
                log.warn("目录创建失败！dir->{}", saveFile.getPath());
            }
        }
        try (FileOutputStream fos = new FileOutputStream(saveFile + File.separator + "bank.xlsx")) {
            Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, null),
                    BankExcelModule.class, allImportList);
            workbook.write(fos);
            workbook.close();
        } catch (Exception e) {
            log.error("导出异常！msg->{}", e.getMessage());
        }
        log.info("任务结束...");
    }
}
