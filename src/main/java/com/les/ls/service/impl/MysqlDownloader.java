package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;

/**
 * mysql方式下载器
 *
 * @author levis
 */
public class MysqlDownloader extends AbstractDBDownloader {

    public MysqlDownloader(JfSetting setting) {
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
     * 获取数据库连接
     *
     * @return 数据库连接对象
     */
    private Connection getConnection() {
        String targetAddress = setting.getDownloadConfig().getString("targetAddress");
        String account = setting.getDownloadConfig().getString("account");
        String password = setting.getDownloadConfig().getString("password");
        String database = setting.getDownloadConfig().getString("database");
        //String dbVersion = setting.getDownloadConfig().getString("dbVersion");
        String driverName, url;
        driverName = "com.mysql.cj.jdbc.Driver";
        url = "jdbc:mysql://"
                .concat(targetAddress).concat("/").concat(database)
                .concat("?useSSL=false&serverTimezone=GMT&useUnicode=true&characterEncoding=UTF-8");
        try {
            Class.forName(driverName);
            return DriverManager.getConnection(url, account, password);
        } catch (Exception e) {
            logger.error("连接数据库失败！", e);
        }
        return null;
    }
}
