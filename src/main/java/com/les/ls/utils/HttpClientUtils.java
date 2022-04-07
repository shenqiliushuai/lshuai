package com.les.ls.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;


/**
 * 自己写的封装，满足一下简单的场景，偶尔用下，若大量使用httpclient，使用下边的httpClientUtil工具包
 * <p color="red">
 * location : https://mvnrepository.com/artifact/com.arronlong/httpclientutil
 *
 * <dependency>
 * <groupId>com.arronlong</groupId>
 * <artifactId>httpclientutil</artifactId>
 * <version>1.0.4</version>
 * </dependency>
 *
 * </p>
 */
public class HttpClientUtils {


    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = getHttpsClient()) {
            HttpPost httpPost = new HttpPost();
            httpClient.execute(httpPost);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 一分钟超时
     */
    private static final int TIMEOUT = 60000;

    /**
     * @param url             请求url
     * @param headerMap       请求头
     * @param param           请求参数
     * @param contentType     请求体内容类型
     * @param timeout         超时时间
     * @param responseCharset 响应内容编码，默认为UTF-8
     * @param isHttps         是否为https
     * @param fileName        https时选填 证书文件地址+名称，为空时绕过证书
     * @param password        https时选填 证书秘钥，为空时绕过证书
     * @return 请求结果
     * @throws Exception exception
     */
    public static String doPost(String url, Map<String, String> headerMap, String param,
                                ContentType contentType, int timeout, Charset responseCharset,
                                boolean isHttps, String fileName, String password) throws Exception {
        String resultString = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = isHttps ? getHttpsClient(fileName, password) : HttpClients.createDefault()) {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            HttpPost httpPost = new HttpPost(builder.build());
            setHttpConfig(httpPost, timeout);
            for (Map.Entry<String, String> elem : headerMap.entrySet()) {
                httpPost.addHeader(elem.getKey(), elem.getValue());
            }
            httpPost.setEntity(new StringEntity(param, contentType));
            response = httpclient.execute(httpPost);
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (responseCharset == null) {
                        responseCharset = StandardCharsets.UTF_8;
                    }
                    resultString = EntityUtils.toString(response.getEntity(), responseCharset);
                }
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * GET请求处理
     *
     * @param url             请求url
     * @param paramMap        请求参数
     * @param timeout         超时时间
     * @param responseCharset 响应内容编码，默认为UTF-8
     * @param isHttps         是否为https
     * @param fileName        https时选填 证书文件地址+名称，为空时绕过证书
     * @param password        https时选填 证书秘钥，为空时绕过证书
     * @return 请求结果
     * @throws Exception exception
     */
    public static String doGet(String url, Map<String, String> paramMap, int timeout,
                               Charset responseCharset, boolean isHttps, String fileName,
                               String password) throws Exception {
        String resultString = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = isHttps ? getHttpsClient(fileName, password) : HttpClients.createDefault()) {
            URIBuilder builder = new URIBuilder(url);
            if (paramMap != null) {
                for (Map.Entry<String, String> elem : paramMap.entrySet()) {
                    builder.addParameter(elem.getKey(), elem.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(builder.build());
            setHttpConfig(httpGet, timeout);
            response = httpclient.execute(httpGet);
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (responseCharset == null) {
                        responseCharset = StandardCharsets.UTF_8;
                    }
                    resultString = EntityUtils.toString(response.getEntity(), responseCharset);
                }
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

    /**
     * http基础配置
     *
     * @param httpRequestBase http请求
     * @param timeout         超时时间
     */
    private static void setHttpConfig(HttpRequestBase httpRequestBase, int timeout) {
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).build();
        httpRequestBase.setConfig(defaultRequestConfig);
    }


    /**
     * 获取一个HTTPS连接客户端 绕过SSL无参调用
     *
     * @return HttpClient
     * @throws Exception 创建连接过程中可能出现的异常
     */
    public static CloseableHttpClient getHttpsClient() throws Exception {
        return getHttpsClient(null, null);
    }


    /**
     * 获取一个HTTPS连接客户端
     *
     * @param fileName 证书文件名称 为空时创建跳过SSL
     * @param password 证书秘钥 为空时创建跳过SSL
     * @return HttpClient
     * @throws Exception 创建连接过程中可能出现的异常
     */
    public static CloseableHttpClient getHttpsClient(String fileName, String password) throws Exception {
        SSLContext sslContext;
        if (StringUtils.isEmpty(fileName) || StringUtils.isEmpty(password)) {
            sslContext = createIgnoreVerifySSL();
        } else {
            sslContext = getSSLContext(fileName, password);
        }
        //设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        return HttpClients.custom().setConnectionManager(connManager).build();
    }


    /**
     * 绕过SSL验证 HttpClient 4.3.x 版本之前可用，之后的版本默认支持https
     *
     * @return SSLContext
     * @throws NoSuchAlgorithmException 找不到算法类型时抛出
     * @throws KeyManagementException   秘钥错误时抛出
     */
    public static SSLContext createIgnoreVerifySSL() throws NoSuchAlgorithmException, KeyManagementException {
        //算法可更换
        SSLContext sc = SSLContext.getInstance("SSLv3");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                    String paramString) {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{trustManager}, null);
        return sc;
    }

    /**
     * 生产环境下读取证书文件并构建SSLContext
     *
     * @param fileName 证书文件路径及名称
     * @param password 证书秘钥
     * @return SSLContext
     * @throws KeyStoreException        秘钥错误时抛出
     * @throws NoSuchAlgorithmException 找不到算法类型时抛出
     * @throws IOException              秘钥文件读取错误时抛出
     * @throws CertificateException     证书错误时抛出
     * @throws KeyManagementException   秘钥管理错误时抛出
     */
    public static SSLContext getSSLContext(String fileName, String password) throws
            KeyStoreException, NoSuchAlgorithmException,
            IOException, CertificateException,
            KeyManagementException {
        //秘钥文件,秘钥
        return SSLContexts.custom()
                .loadTrustMaterial(new File(fileName), password.toCharArray())
                .build();
    }

}
