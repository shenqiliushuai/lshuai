package com.les.ls.service.impl;

import com.les.ls.pojo.dto.DownloadResult;
import com.les.ls.pojo.dto.Placeholder;
import com.les.ls.pojo.po.JfSetting;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Http方式下载器
 *
 * @author levis
 */
public class HttpDownloader extends AbstractDownloader {

    //三分钟超时
    private final int TIMEOUT = 180 * 1000;

    HttpDownloader(JfSetting setting) {
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
        String result;
        if ("1".equals(setting.getDownloadConfig().getString("isWebservice"))) {
            result = doWebService(false, place);
        } else {
            if ("GET".equals(setting.getDownloadConfig().getString("httpMethod"))) {
                result = doGet(false, place);
            } else {
                result = doPost(false, place);
            }
        }
        logger.debug("HTTP请求返回结果result->{}", result);
        // 如果返回报文中存在换行符，需要保留指定行数
        Pattern pattern = Pattern.compile(System.lineSeparator());
        if (result != null && pattern.matcher(result).find()) {
            Integer downloadNum = setting.getDownloadConfig().getInteger("downloadNum");
            if (downloadNum != null) {
                String[] lines = result.split(System.lineSeparator());
                if (lines.length >= downloadNum) {
                    result = Arrays.asList(lines)
                            .subList(0, downloadNum)
                            .stream()
                            .collect(Collectors.joining(System.lineSeparator()));
                }
            }
        }
        //处理结果集
        result = disposeResult(result);
        logger.debug("结果集处理后result->{}", result);
        DownloadResult downloadResult = new DownloadResult();
        downloadResult.setType(DownloadResult.TYPE_DATA);
        downloadResult.setData(result);
        return downloadResult;
    }

    /**
     * 网络测试
     *
     * @return 测试结果
     */
    @Override
    public Boolean testConnect() {
        //test无需占位符
        Placeholder placeholder = new Placeholder();
        if ("1".equals(setting.getDownloadConfig().getString("isWebservice"))) {
            return StringUtils.isNotEmpty(doWebService(true, placeholder));
        } else {
            if ("GET".equals(setting.getDownloadConfig().getString("httpMethod"))) {
                return StringUtils.isNotEmpty(doGet(true, placeholder));
            } else {
                return StringUtils.isNotEmpty(doPost(true, placeholder));
            }
        }
    }

    /**
     * GET请求处理
     *
     * @param isTest      是否测试
     * @param placeholder 占位符对象
     * @return 测试结果 OR 请求结果
     */
    private String doGet(boolean isTest, Placeholder placeholder) {
        String resultString = null;
        CloseableHttpResponse response = null;
        String url = setting.getDownloadConfig().getString("targetAddress");
        Map<String, String> headerMap = getHeaderMap();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            // 请求头参数拼进URI
            for (Map.Entry<String, String> elem : headerMap.entrySet()) {
                //处理占位符，在值中
                String value = disposePlaceholder(elem.getValue(), placeholder);
                builder.addParameter(elem.getKey(), value);
            }
            // 创建GET请求
            HttpGet httpGet = new HttpGet(builder.build());
            // 请求相关基础配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
                    .setConnectionRequestTimeout(TIMEOUT).build();
            httpGet.setConfig(defaultRequestConfig);
            // 执行请求
            response = httpclient.execute(httpGet);
            if (isTest) {
                return response != null ? "测试成功！" : "测试失败！";
            }
            //有可能无响应
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    logger.warn("响应异常！httpStatus->{}", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            logger.error("请求异常！", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("释放资源失败！", e);
            }
        }
        return resultString;
    }

    /**
     * POST请求处理
     *
     * @param isTest      是否测试
     * @param placeholder 占位符对象
     * @return 测试结果 OR 请求结果
     */
    private String doPost(boolean isTest, Placeholder placeholder) {
        CloseableHttpResponse response = null;
        String resultString = null;
        String url = setting.getDownloadConfig().getString("targetAddress");
        String httpBody = setting.getDownloadConfig().getString("httpBody");
        Map<String, String> headerMap = getHeaderMap();
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
                    .setConnectionRequestTimeout(TIMEOUT).build();
            // 请求相关基础配置
            httpPost.setConfig(defaultRequestConfig);
            //增加请求头
            for (Map.Entry<String, String> elem : headerMap.entrySet()) {
                httpPost.addHeader(elem.getKey(), elem.getValue());
            }
            //占位符处理
            disposePlaceholder(httpBody, placeholder);
            // 创建请求体
            StringEntity entity = new StringEntity(httpBody);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            if (isTest) {
                return response != null ? "测试成功！" : "测试失败！";
            }
            //有可能无响应
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    logger.warn("响应异常！httpStatus->{}", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            logger.error("请求失败！", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("请求资源关闭异常！", e);
            }
        }
        return resultString;
    }

    /**
     * webService请求处理
     *
     * @param isTest      是否测试
     * @param placeholder 占位符对象
     * @return 测试结果 OR 请求结果
     */
    private String doWebService(boolean isTest, Placeholder placeholder) {
        String resultString = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = setting.getDownloadConfig().getString("targetAddress");
            String httpBody = setting.getDownloadConfig().getString("httpBody");
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 请求相关基础配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT)
                    .setConnectionRequestTimeout(TIMEOUT).build();
            httpPost.setConfig(defaultRequestConfig);
            // 增加请求头
            Map<String, String> headerMap = getHeaderMap();
            for (Map.Entry<String, String> elem : headerMap.entrySet()) {
                httpPost.addHeader(elem.getKey(), elem.getValue());
            }
            //占位符处理
            disposePlaceholder(httpBody, placeholder);
            // 创建请求内容
            StringEntity entity = new StringEntity(httpBody, ContentType.TEXT_XML);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            if (isTest) {
                return response != null ? "测试成功！" : "测试失败！";
            }
            //有可能无响应
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    resultString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                } else {
                    logger.warn("响应异常！httpStatus->{}", response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            logger.error("请求失败！", e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error("请求资源关闭异常！", e);
            }
        }
        return resultString;
    }

    /**
     * 通过配置获取请求头 MAP
     *
     * @return 请求头MAP
     */
    private Map<String, String> getHeaderMap() {
        String httpHeader = setting.getDownloadConfig().getString("httpHeader");
        Map<String, String> headerMap = new HashMap<>();
        if (StringUtils.isNotEmpty(httpHeader)) {
            String[] headerLines = httpHeader.split(System.lineSeparator());
            for (String headerLine : headerLines) {
                String[] kv = headerLine.split(":");
                headerMap.put(kv[0], kv[1]);
            }
        }
        return headerMap;
    }

    /**
     * 处理返回结果的转义字符、数据集等
     *
     * @param result 返回结果
     * @return 处理结果
     */
    private String disposeResult(String result) {
        if (result == null) {
            return result;
        }
        result = result.replaceAll("&lt;", "<");
        result = result.replaceAll("&gt;", ">");
        int startIndex = result.indexOf("<![CDATA[");
        if (startIndex == -1) {
            //无CDATA
            return result;
        }
        int endIndex = result.indexOf("]]>");
        if (endIndex == -1) {
            //有可能保留行导致了尾部截断，不包含尾部不做处理
            result = result.substring(startIndex + 9);
            return result;
        }
        return result.substring(startIndex + 9, endIndex);
    }
}
