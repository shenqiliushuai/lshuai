package com.les.shengkai.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.les.ls.utils.TerminalUtils;
import com.les.shengkai.pojo.ShengKaiExcelExport;
import com.les.shengkai.pojo.ShengkaiExcelImport;
import com.les.shengkai.service.ShengKaiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ShengKaiServiceImpl implements ShengKaiService {
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
        for (File file : files) {
            if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx")) {
                List<ShengkaiExcelImport> importList = ExcelImportUtil.importExcel(file, ShengkaiExcelImport.class, params);
                List<ShengKaiExcelExport> exportList = new ArrayList<>();
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
                log.info("文件{}共读取{}行,处理完成{}行，正在导出...", file.getName(), importList.size(), exportList.size());
                String exportFileName = file.getName().split("\\.")[0];
                int fileSplitSize = 5000, index = 0;
                do {
                    index++;
                    List<ShengKaiExcelExport> splitList = exportList.subList(0, Math.min(exportList.size(), fileSplitSize));
                    File saveFile = new File(workDir + File.separator + "export");
                    if (!saveFile.exists()) {
                        if (!saveFile.mkdirs()) {
                            log.warn("目录创建失败！dir->{}", saveFile.getPath());
                        }
                    }
                    try (FileOutputStream fos = new FileOutputStream(saveFile + File.separator + index + exportFileName + ".csv")) {
                        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams(null, null, exportFileName),
                            ShengKaiExcelExport.class, splitList);
                        workbook.write(fos);
                        workbook.close();
                    } catch (Exception e) {
                        log.error("导出异常！msg->{}", e.getMessage());
                    }
                    log.info("文件{}导出成功！", index + file.getName());
                } while (exportList.size() != 0);
            } else {
                log.warn("已跳过文件{}", file.getName());
            }
        }
    }
}
