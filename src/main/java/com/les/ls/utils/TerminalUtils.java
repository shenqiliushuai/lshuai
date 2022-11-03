package com.les.ls.utils;

import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 执行终端命令工具类
 *
 * @author lshuai
 */
public class TerminalUtils {

    public static void main(String[] args) {
        boolean pingResult = pingTest("www.baidu.com");
        System.out.println(pingResult);
        String execResult = execForLinux(",", "java -version");
        System.out.println(execResult);
        System.out.println(getWorkDir());
    }

    private static final long TIMEOUT = 10;

    public static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取工作目录
     *
     * @return 工作目录路径
     */
    public static String getWorkDir() {
        return System.getProperty("user.dir") + File.separator;
    }

    /**
     * ping测试，支持IP和域名
     *
     * @return 测试结果
     */
    public static boolean pingTest(String address) {
        try {
            //InetAddress方法不支持域名
            //InetAddress.getByName(getRemoteAddress(setting.getAuthConfig())).isReachable(10000);
            return Runtime.getRuntime().exec("ping -c 1 " + address).waitFor(TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * windows平台下执行 cmd指令
     *
     * @param command cmd指令
     * @return 命令执行结果
     */
    public static String execCommand(String command) {
        String result = "";
        try {
            List<String> cmdList = new ArrayList<>();
            cmdList.add("cmd.exe");
            cmdList.add("/c");
            cmdList.add(command);
            ProcessBuilder builder = new ProcessBuilder(cmdList);
            // 重定向错误输出
            builder.redirectErrorStream(true);
            Process process = builder.start();
            result = getStreamStr(process.getInputStream());
            // 等待进程对象执行完成，并返回 退出值，0 为正常，其他为异常
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStreamStr(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")))) {
            return br.lines().collect(Collectors.joining());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 执行Linux命令获取结果，命令集必须存在值，否则返回空字符串
     *
     * @param spStr  结果按行分割符 可为空，为空时不做拼接
     * @param cmdStr 命令集
     * @return 命令执行结果
     */
    public static String execForLinux(String spStr, String... cmdStr) {
        StringBuilder result = new StringBuilder();
        Process process = null;
        if (cmdStr != null && cmdStr.length > 0) {
            try {
                //数组和非数组分支不一致，一个值默认是非数组
                if (cmdStr.length == 1) {
                    process = Runtime.getRuntime().exec(cmdStr[0]);
                } else {
                    process = Runtime.getRuntime().exec(cmdStr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "";
        }
        if (process == null) {
            return "";
        }
        boolean flag = false;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), UTF_8))) {
            String tempStr;
            while ((tempStr = bufferedReader.readLine()) != null) {
                flag = true;
                result.append(tempStr);
                if (StringUtils.hasLength(spStr)) {
                    result.append(spStr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!flag) {
            //正确输出流无数据，可能给到错误流中
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), UTF_8))) {
                String tempStr;
                while ((tempStr = bufferedReader.readLine()) != null) {
                    result.append(tempStr);
                    if (StringUtils.hasLength(spStr)) {
                        result.append(spStr);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        process.destroy();
        return result.toString();
    }
}
