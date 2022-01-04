package com.les.ls.service.impl;


import com.les.ls.pojo.dto.Placeholder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDBDownloader extends AbstractDownloader {

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
