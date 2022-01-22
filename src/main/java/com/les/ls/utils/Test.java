package com.les.ls.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {

        String str = "2022-01-12 11:53:01.386 [http-nio-8083-exec-5] INFO  96 c.c.ihpl.hcnss.config.ControllerLoggingAspect-logId=1, ]result={\"code\":0,\"message\":\"success\",\"result\":{\"synchronizationResult\":true,\"id\":42}}";
        Object[] test = new Object[]{str};
        applyLogColor(test);
        System.out.println(test[0]);
    }

    private static void applyLogColor(Object[] logLines) {
        for (int i = 0; i < logLines.length; i++) {
            String line = (String) logLines[i];
            //先 处理等级
            line = line.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
            line = line.replace("INFO", "<span style='color: green;'>INFO</span>");
            line = line.replace("WARN", "<span style='color: orange;'>WARN</span>");
            line = line.replace("ERROR", "<span style='color: red;'>ERROR</span>");
            //第一级 处理日志前缀
            int oneIndex = line.indexOf("]");
            String prefix = line.substring(0, oneIndex);
            //中间部分
            String center = line.substring(oneIndex);
            //第二级 处理类名
            int twoIndex = center.indexOf("-");
            String className = center.substring(0, twoIndex);
            line = prefix + "]" + "<span style='color: #298a8a;'>" + className + "</span>" + center.substring(twoIndex);
            logLines[i] = line;
        }
    }
}
