package com.les.ls.utils.Thread;

import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 读取项目运行日志
 */
@Slf4j
public class ReadLogThead implements Runnable {


    public ReadLogThead(Session session) {
        this.session = session;
    }

    private final Session session;
    /**
     * 如果日志换了目录，这里需要同步更改。
     */
    private final String LOG_FILE_PATH = System.getProperty("user.dir") + File.separator + "logs";

    /**
     * 日志集合
     *//*
    public static final Map<String, Integer> lengthMap = new ConcurrentHashMap<>();*/

    private BufferedReader bufferedReader = null;
    private String oldLogFileName = null;

    @Override
    public void run() {
        boolean first = true;
        try {
            while (session.isOpen()) {
                File[] files = new File(LOG_FILE_PATH).listFiles();
                if (files == null || files.length == 0) {
                    log.warn("日志目录无文件！LOG_FILE_PATH->{}", LOG_FILE_PATH);
                    return;
                }
                //文件名称规则 analysis-tool.2022-01-05.0.log
                String firstPrefix = "analysis-tool.";
                String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                //文件名匹配规则
                String fileNameRule = firstPrefix + todayDate;
                //过滤掉不匹配的日志文件，拿到最新的日志文件
                List<File> fileList = Arrays.stream(files)
                        .filter(file -> file.getName().startsWith(fileNameRule))
                        .sorted(Comparator.comparing(File::lastModified))
                        .collect(Collectors.toList());
                Collections.reverse(fileList);
                //新日志产生时会有一点间隙，日期切换，日志文件还未产生，这里需要打印一个日志并跳过。
                if (fileList.isEmpty()) {
                    log.info("{}日志开始...", todayDate);
                    Thread.sleep(500);
                    continue;
                }
                File newLogFile = fileList.get(0);
                //检查文件切换
                checkFileModify(newLogFile);
                if (newLogFile.canRead()) {
                    Object[] lines = bufferedReader.lines().toArray();
                    //只取从上次之后产生的日志
                    Object[] copyOfRange = Arrays.copyOfRange(lines, 0, lines.length);
                    //抛弃第一次读到的数据，只会进一次
                    if (first) {
                        log.info("已跳过历史日志{}行！", copyOfRange.length);
                        first = false;
                        continue;
                    }
                    //暂无新日志产生，就暂停0.5秒刷新日志，否则一直读文件
                    if (copyOfRange.length == 0) {
                        Thread.sleep(500);
                    }
                    //日志渲染及输出
                    applyLogColor(copyOfRange);
                    for (Object o : copyOfRange) {
                        if (session.isOpen()) {
                            session.getBasicRemote().sendText((String) o);
                        }
                    }
                } else {
                    //有可能权限导致文件不可读
                    log.warn("该文件不可读取！logFile->{}", newLogFile.getPath());
                }
            }
        } catch (Exception e) {
            log.error("websocket发送日志异常！", e);
        } finally {
            closeBufferedReader();
        }
    }

    private static void applyLogColor(Object[] logLines) {
        for (int i = 0; i < logLines.length; i++) {
            String line = (String) logLines[i];
            try {
                //先 处理等级
                line = line.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
                line = line.replace("INFO", "<span style='color: green;'>INFO</span>");
                line = line.replace("WARN", "<span style='color: orange;'>WARN</span>");
                line = line.replace("ERROR", "<span style='color: red;'>ERROR</span>");
                //第一级 处理日志前缀
                int oneIndex = line.indexOf("]") + 1;
                //找不到就返回原字串
                if (oneIndex == 0) {
                    continue;
                }
                String prefix = line.substring(0, oneIndex);
                //中间部分
                String center = line.substring(oneIndex);
                //第二级 处理类名
                int twoIndex = center.indexOf("-");
                if (twoIndex == -1) {
                    continue;
                }
                String className = center.substring(0, twoIndex);
                line = prefix + "<span style='color: #298a8a;'>" + className + "</span>" + center.substring(twoIndex);
            } catch (Exception e) {
                log.error("日志渲染错误！line->{}", line, e);
            } finally {
                logLines[i] = line;
            }
        }
    }

    /**
     * 检查日志文件是否切换
     *
     * @param newLogFile 新的日志文件
     */
    private void checkFileModify(File newLogFile) {
        //判断日志文件切换时机
        //文件名相等且reader不为空就用打开的bufferedReader
        if (bufferedReader != null && oldLogFileName.equals(newLogFile.getName())) {
            return;
        }
        try {
            //关掉之前的，打开新的
            closeBufferedReader();
            oldLogFileName = newLogFile.getName();
            bufferedReader = new BufferedReader(new FileReader(newLogFile));
        } catch (Exception e) {
            log.error("文件读取异常！newLogFile->{}", newLogFile);
        }
    }

    /**
     * 关闭文件流
     */
    private void closeBufferedReader() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception e) {
                log.error("释放资源异常！bufferedReader->{}", bufferedReader.toString());
            }
        }
    }
}
