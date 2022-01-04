package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.*;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ftp方式下载器
 *
 * @author levis
 */
public class FTPDownloader extends AbstractDownloader {

    FTPDownloader(JfSetting setting) {
        initDownloader(setting);
    }

    /**
     * 下载方法
     *
     * @param place 占位符对象
     * @return 下载结果
     */
    @Override
    public DownloadResult download(Placeholder place) {
        //删除历史文件
        clearHistoryData();
        DownloadResult downloadInfo = new DownloadResult();
        //拿到FTP连接
        FTPClient ftpClient = openFtpConnect();
        if (ftpClient == null) {
            logger.warn("FTP连接失败！");
            return downloadInfo;
        }
        //设置FTP连接属性
        settingFtp(ftpClient, place);
        List<String> list = new ArrayList<>();
        try {
            FTPFile[] ftpFiles = ftpClient.listFiles();
            //修改时间倒排
            List<FTPFile> fileList = new ArrayList<>(Arrays.asList(ftpFiles));
            fileList.sort(Comparator.comparing(FTPFile::getTimestamp));
            Collections.reverse(fileList);
            //下载文件数量限制
            Integer downloadNum = setting.getDownloadConfig().getInteger("downloadNum");
            //默认十个
            downloadNum = downloadNum == null ? 10 : downloadNum;
            int num = 0;
            for (FTPFile ftpFile : fileList) {
                if (downloadNum > num) {
                    String fileName = ftpFile.getName();
                    //文件夹跳过
                    if (ftpFile.isDirectory()) {
                        continue;
                    }
                    //FTP文件名匹配规则
                    String fileNameRule = setting.getDownloadConfig().getString("billRules");
                    //文件名占位符处理
                    String disposeResult = disposePlaceholder(fileNameRule, place);
                    //占位符处理结果做为正则匹配
                    if (!disposeResult.equals(fileName) && !fileName.matches("^" + disposeResult + "$")) {
                        continue;
                    }
                    num++;
                    String outFileName = setting.getOrgCode() + "_" +
                            setting.getChannel() + "_" +
                            setting.getDownloadType() + "_" + fileName;
                    File localFile = new File(billFilePath + File.separator + outFileName);
                    OutputStream outputStream = FileUtils.openOutputStream(localFile);
                    //文件输出
                    ftpClient.retrieveFile(new String(fileName.getBytes("gbk"), StandardCharsets.ISO_8859_1), outputStream);
                    outputStream.flush();
                    outputStream.close();
                    list.add(localFile.getPath());
                }
            }
        } catch (Exception e) {
            logger.error("FTP文件下载异常！", e);
            return downloadInfo;
        } finally {
            closeFtpConnect(ftpClient);
        }
        downloadInfo.setType(DownloadResult.TYPE_FILE_LIST);
        downloadInfo.setFileList(list);
        return downloadInfo;
    }

    /**
     * 连接测试
     *
     * @return 测试结果
     */
    @Override
    public Boolean testConnect() {
        FTPClient ftpClient = openFtpConnect();
        if (ftpClient == null) {
            return false;
        } else {
            closeFtpConnect(ftpClient);
            return true;
        }
    }

    /**
     * 打开FTPClient
     *
     * @return FTPClient
     */
    private FTPClient openFtpConnect() {
        String remoteAddress = getRemoteAddress();
        String remotePort = getRemotePort();
        String username = setting.getDownloadConfig().getString("account");
        String password = setting.getDownloadConfig().getString("password");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(remoteAddress, Integer.parseInt(remotePort));
            ftpClient.login(username, password);
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                logger.info("FTP连接成功。");
            } else {
                closeFtpConnect(ftpClient);
                logger.warn("未连接到FTP，用户名或密码错误。");
                return null;
            }
        } catch (Exception e) {
            logger.error("FTP连接失败！", e);
            return null;
        }
        return ftpClient;
    }

    /**
     * 关闭FTPClient
     *
     * @param ftpClient FTPClient
     */
    private void closeFtpConnect(FTPClient ftpClient) {
        try {
            ftpClient.disconnect();
        } catch (Exception e) {
            logger.error("FTP连接资源关闭异常！", e);
        }
    }

    /**
     * 设置FTP连接属性
     *
     * @param ftpClient FTPClient
     * @param place     占位符对象
     */
    private void settingFtp(FTPClient ftpClient, Placeholder place) {
        try {
            //中文支持
            ftpClient.setControlEncoding("GBK");
            //二进制模式下载文件
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //二进制流传输模式
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            //被动模式
            ftpClient.enterLocalPassiveMode();
            //切换工作目录
            String ftpPath = setting.getDownloadConfig().getString("ftpCatalog");
            if (StringUtils.isNotEmpty(ftpPath)) {
                //占位符处理
                ftpPath = disposePlaceholder(ftpPath, place);
                //切换目录转码
                String decodeFtpPath = new String(ftpPath.getBytes("gbk"), StandardCharsets.ISO_8859_1);
                boolean changeResult = ftpClient.changeWorkingDirectory(decodeFtpPath);
                if (changeResult) {
                    logger.info("FTP工作目录已切换 ftpPath->{}", ftpPath);
                } else {
                    logger.warn("FTP工作目录切换失败！ftpPath->{}", ftpPath);
                }
            }
        } catch (Exception e) {
            logger.error("FTP连接属性设置失败！", e);
        }
    }
}
