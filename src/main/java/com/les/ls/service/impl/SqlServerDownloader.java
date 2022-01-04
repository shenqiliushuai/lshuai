package com.les.ls.service.impl;


import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;

import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * ms-sql方式下载器
 *
 * @author levis
 */
public class SqlServerDownloader extends AbstractDBDownloader {

    SqlServerDownloader(JfSetting setting) {
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
        List<Map<String, Object>> result = executeSql(getConnection(), place);
        DownloadResult downloadResult = new DownloadResult();
        downloadResult.setType(DownloadResult.TYPE_LIST);
        downloadResult.setList(result);
        return downloadResult;
    }

    /**
     * 测试连接
     *
     * @return 测试结果
     */
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
     * 获取数据库连接
     *
     * @return 数据库连接对象
     */
    private Connection getConnection() {
        String targetAddress = setting.getDownloadConfig().getString("targetAddress");
        String account = setting.getDownloadConfig().getString("account");
        String password = setting.getDownloadConfig().getString("password");
        String database = setting.getDownloadConfig().getString("database");
        //"jdbc:sqlserver://127.0.0.1:1433;DatabaseName=test;";
        String url = "jdbc:sqlserver://".concat(targetAddress).concat(";DatabaseName=").concat(database).concat(";");
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        try {
            Class.forName(driverName);
            return DriverManager.getConnection(url, account, password);
        } catch (Exception e) {
            logger.error("连接数据库失败！", e);
        }
        return null;
    }
}
