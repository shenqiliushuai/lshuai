package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

/**
 * Oracle方式下载器
 *
 * @author levis
 */
public class OracleDownloader extends AbstractDBDownloader {

    OracleDownloader(JfSetting setting) {
        initDownloader(setting);
    }

    @Override
    public DownloadResult download(Placeholder place) {
        List<Map<String, Object>> result = executeSql(getConnection(), place);
        DownloadResult downloadResult = new DownloadResult();
        downloadResult.setType(DownloadResult.TYPE_LIST);
        downloadResult.setList(result);
        return downloadResult;
    }

    @Override
    public Boolean testConnect() {
        Connection connection = getConnection();
        if (connection != null) {
            closeConnection(connection);
            return true;
        }
        return false;
    }

    /**
     * 获取oracle连接
     *
     * @return 连接对象
     */
    private Connection getConnection() {
        String targetAddress = setting.getDownloadConfig().getString("targetAddress");
        String account = setting.getDownloadConfig().getString("account");
        String password = setting.getDownloadConfig().getString("password");
        String database = setting.getDownloadConfig().getString("database");
        String driverName, url;
        driverName = "oracle.jdbc.driver.OracleDriver";
        //jdbc:oracle:thin:@192.168.1.180:1521:orcl
        url = "jdbc:oracle:thin:@".concat(targetAddress).concat(":").concat(database);
        try {
            Class.forName(driverName);
            return DriverManager.getConnection(url, account, password);
        } catch (Exception e) {
            logger.error("连接数据库失败！", e);
        }
        return null;
    }
}
