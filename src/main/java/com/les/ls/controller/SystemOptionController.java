package com.les.ls.controller;

import com.les.ls.config.ConstantsConfig;
import com.les.ls.utils.DateUtils;
import com.les.ls.utils.TerminalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/system")
public class SystemOptionController {

    @Resource
    private ConstantsConfig constants;

    @GetMapping("/shutdown")
    public void shutdown() {
        System.exit(0);
    }

    /**
     * <p><font color="red">
     * 1. 检查 schtasks 创建的自动任务是否存在，不存在创建，存在则修改时间</br>
     * 2. schtasks /query /tn "my once"  查询任务是否存在</br>
     * 3. schtasks /create /tn "my once" /tr D:\WinSCP\WinSCP.exe /sc once /st 17:23:00 /sd 2022/10/31  创建任务</br>
     * 4. schtasks 执行远程主机的计划任务提示拒绝访问一般由两个因素：1.登录用户没有权限。2.组策略设置不得当。</br>
     * 5. 建议用administrator账户登录，并且更改本地组策略：
     * 运行gpedit.msc ——计算机配置——Windows设置——安全设置——本地策略 ——安全选项——用户账户控制：以管理员批准模式运行所有管理员——禁用</br>
     * </font></p>
     */
    @GetMapping("{version}/restart")
    public void restart(@PathVariable String version) {
        String os = TerminalUtils.getOSName();
        String[] futureDateTimes = DateUtils.getFutureDateTime(120L, DateUtils.yyyy_MM_dd_HH_mm_ss2).split(" ");
        String futureDate = futureDateTimes[0], futureTime = futureDateTimes[1];
        if (os.contains("windows")) {
            if ("v1".equals(version)) {
                //v1版本使用 schtasks命令的实现方式需要自己写bat脚本，并且确保权限问题。在自启动会有相当大的麻烦
                String queryTaskResult = TerminalUtils.execCommand("schtasks /query /tn \"" + constants.windowsRestartTaskName + "\"");
                if (queryTaskResult.contains("系统找不到")) {
                    //找不到任务，执行任务创建
                    String createTaskResult = TerminalUtils.execCommand("schtasks /create" +
                            " /tn \"" + constants.windowsRestartTaskName + "\"" +  // tn指定任务名称
                            " /tr " + constants.windowsRestartScriptName +            // tr指定任务执行内容
                            " /sc once " +                                       // sc执行策略，只执行一次
                            " /sd " + futureDate +                                 // sd执行时间
                            " /st " + futureTime);                                    // st执行日期
                    if (!createTaskResult.contains("成功创建计划任务")) {
                        log.error("任务创建失败！createTaskResult->{}", createTaskResult);
                    }
                } else {
                    //任务存在，修改任务执行时间
                    String changeTaskResult = TerminalUtils.execCommand("echo " + constants.windowsSystemPassword + "| " +
                            "schtasks /change " +
                            " /tn \"" + constants.windowsRestartTaskName + "\" " +
                            " /sd " + futureDate +
                            " /st " + futureTime);
                    if (!changeTaskResult.contains("成功")) {
                        log.error("任务修改失败！changeTaskResult->{}", changeTaskResult);
                    }
                }
                //关闭应用程序，剩下的交给任务脚本
                System.exit(0);
            } else if ("v2".equals(version)) {
                // v2版本使用WinSW 工具实现打包发布java程序为windows service服务，使用命令重启即可
                String checkResult = TerminalUtils.execCommand(constants.windowsCheckCmdForService);
                if (checkResult.contains("running")) {
                    TerminalUtils.execCommand(constants.windowsRestartCmdForService);
                } else {
                    log.error("not found service ! checkResult->{}", checkResult);
                }
            } else {
                log.error("not found version ! version->{}", version);
            }
        } else if (os.contains("linux")) {
            //检查 atd服务， 执行at命令
            String checkResult = TerminalUtils.execForLinux(null, "/bin/sh", "-c", "systemctl is-active atd");
            if (!"active".equals(checkResult)) {
                //启动atd服务
                TerminalUtils.execForLinux(null, "/bin/sh", "-c", "systemctl start atd");
                //再次检查
                checkResult = TerminalUtils.execForLinux(null, "/bin/sh", "-c", "systemctl is-active atd");
                if (!"active".equals(checkResult)) {
                    log.error("无法启动 atd 服务！");
                    return;
                }
            }
            String createTaskResult = TerminalUtils.execForLinux(null, "/bin/sh", "-c",
                    "at -f " + constants.linuxRestartScriptName + " now");
            if (createTaskResult.contains("error")) {
                log.error("执行失败！检查执行脚本是否存在！");
            }
        } else {
            log.error("系统未识别，无法处理！os->{}", os);
        }
    }

    public static void main(String[] args) throws Exception {

    }
}
