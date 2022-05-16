package com.les.shengkai.controller;

import com.les.shengkai.service.ShengKaiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 给盛凯做Excel转换
 */
@RestController
@RequestMapping("/shengkai")
public class ExcelParserController {

    @Resource
    private ShengKaiService shengKaiService;

    @GetMapping("/excelParser")
    public String doParser() {
        shengKaiService.doExcelParser();
        return "处理成功！";
    }
}
