package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;
import com.les.ls.service.DownloadService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * 下载器抽象类
 *
 * @author levis
 */
public abstract class AbstractDownloader implements DownloadService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //配置对象
    public JfSetting setting;
    //附件下载位置
    public String billFilePath;


    @Override
    public DownloadResult download(JfSetting setting, Placeholder placeholder) {
        AbstractDownloader downloader = getInstance(setting);
        return downloader.download(placeholder);
    }

    @Override
    public void saveConfig() {

    }

    /**
     * 通过配置获取指定实例
     *
     * @param setting 配置对象
     * @return 实例对象
     */
    public static AbstractDownloader getInstance(JfSetting setting) {
        AbstractDownloader download = null;
        switch (setting.getDownloadType()) {
            case "email":
                download = new EmailDownloader(setting);
                break;
            case "ftp":
                download = new FTPDownloader(setting);
                break;
            case "http":
                download = new HttpDownloader(setting);
                break;
            case "mysql":
                download = new MysqlDownloader(setting);
                break;
            case "oracle":
                download = new OracleDownloader(setting);
                break;
            case "sqlserver":
                download = new SqlServerDownloader(setting);
                break;
            default:
                break;
        }
        return download;
    }


    /**
     * 初始化下载类通用参数
     *
     * @param setting 配置对象
     */
    public void initDownloader(JfSetting setting) {
        //文件存储路径，名称 running dir/billFile/机构ID_渠道编码_下载方式_文件名
        this.billFilePath = System.getProperty("user.dir") + File.separator + "billFile";
        this.setting = setting;
    }

    /**
     * 下载方法
     *
     * @param place 占位符对象
     * @return 下载结果
     */
    public abstract DownloadResult download(Placeholder place);

    /**
     * 清理历史文件
     */
    public void clearHistoryData() {
        File[] files = new File(billFilePath).listFiles();
        String fileNamePrefix = setting.getOrgCode() + "_" +
                setting.getChannel() + "_" +
                setting.getDownloadType() + "_";
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(fileNamePrefix)) {
                    boolean result = FileUtils.deleteQuietly(file);
                    if (result) {
                        logger.info("已清理历史文件 billFile->{}", file.getPath());
                    } else {
                        logger.warn("清理历史文件失败！billFile->{}", file.getPath());
                    }
                }
            }
        }
    }

    /**
     * ping测试
     *
     * @return 测试结果
     */
    public Boolean testPing() {
        try {
            return InetAddress.getByName(getRemoteAddress()).isReachable(10000);
        } catch (Exception e) {
            logger.error("ping测试失败！", e);
            return false;
        }
    }

    /**
     * telnet测试
     *
     * @return 测试结果
     */
    @Override
    public Boolean testTelnet() {
        TelnetClient telnet = new TelnetClient();
        String remoteAddress = getRemoteAddress();
        String remotePort = getRemotePort();
        try {
            telnet.connect(remoteAddress, Integer.parseInt(remotePort));
        } catch (Exception e) {
            logger.error("telnet连接失败！", e);
            return false;
        }
        return telnet.isConnected() && telnet.isAvailable();
    }

    /**
     * 通过配置获取地址
     *
     * @return 地址
     */
    public String getRemoteAddress() {
        String remoteIp;
        if (setting.getDownloadType().equals("email")) {
            remoteIp = setting.getDownloadConfig().getString("emailServer");
        } else {
            remoteIp = setting.getDownloadConfig().getString("targetAddress");
            remoteIp = remoteIp.substring(0, remoteIp.lastIndexOf(":"));
            if (remoteIp.contains("/")) {
                remoteIp = remoteIp.substring(remoteIp.lastIndexOf("/") + 1);
            }
        }
        return remoteIp;
    }

    /**
     * 通过配置获取端口
     *
     * @return 端口
     */
    public String getRemotePort() {
        String remotePort;
        if (setting.getDownloadType().equals("email")) {
            remotePort = setting.getDownloadConfig().getString("emailPort");
        } else {
            String address = setting.getDownloadConfig().getString("targetAddress");
            remotePort = address.substring(address.lastIndexOf(":") + 1);
            if (remotePort.contains("/")) {
                remotePort = remotePort.substring(0, remotePort.indexOf("/"));
            }
        }
        return remotePort;
    }

    /**
     * 处理字符串占位符
     *
     * @param srcStr      源字符串
     * @param placeholder 占位符对象
     * @return 处理结果
     */
    public String disposePlaceholder(String srcStr, Placeholder placeholder) {
        if (placeholder == null) {
            return srcStr;
        }
        try {
            if (srcStr.contains("$")) {
                Field[] fields = placeholder.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == String.class) {
                        field.setAccessible(true);
                        if (field.get(placeholder) != null) {
                            srcStr = srcStr.replaceAll("\\$\\{" + field.getName() + "}", field.get(placeholder).toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("占位符处理异常！", e);
        }
        return srcStr;
    }

    /**
     * 处理占位符。sql需要加单引号
     *
     * @param srcStr      源字符串
     * @param placeholder 占位符对象
     * @return 处理结果
     */
    public String disposePlaceholderForSql(String srcStr, Placeholder placeholder) {
        if (placeholder == null) {
            return srcStr;
        }
        try {
            if (srcStr.contains("$")) {
                Field[] fields = placeholder.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == String.class) {
                        field.setAccessible(true);
                        if (field.get(placeholder) != null) {
                            srcStr = srcStr.replaceAll("\\$\\{" + field.getName() + "}",
                                    "'" + field.get(placeholder).toString() + "'");
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("占位符处理异常！", e);
        }
        return srcStr;
    }

    /**
     * 执行SQL得到返回结果
     *
     * @param placeholder 占位符对象
     * @return SQL执行结果
     */
    public List<Map<String, Object>> executeSql(Connection connection, Placeholder placeholder) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (connection == null) {
            return mapList;
        }
        String sql = setting.getDownloadConfig().getString("sql");
        //处理占位符
        sql = disposePlaceholderForSql(sql, placeholder);
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            //使用mateData获取列
            ResultSetMetaData metaData = resultSet.getMetaData();
            //返回行限制
            Integer downloadNum = setting.getDownloadConfig().getInteger("downloadNum");
            //默认100行
            downloadNum = downloadNum == null ? 100 : downloadNum;
            while (resultSet.next() && mapList.size() < downloadNum) {
                //保证输出列顺序一致，使用LinkedHashMap
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    Object columnValue = resultSet.getObject(columnLabel);
                    map.put(columnLabel, columnValue);
                }
                mapList.add(map);
            }
        } catch (Exception e) {
            logger.error("执行sql错误！", e);
        } finally {
            closeConnection(connection);
        }
        return mapList;
    }

    /**
     * 关闭连接
     *
     * @param connection 数据库连接
     */
    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (Exception e) {
            logger.error("数据库连接关闭失败！", e);
        }
    }
}
