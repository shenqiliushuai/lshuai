package com.les.ls.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
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
     * GET请求处理
     *
     * @param url             请求url
     * @param headerMap       请求参数
     * @param responseCharset 响应内容编码，默认为UTF-8
     * @param isHttps         是否为https
     * @param fileName        https时选填 证书文件地址+名称，为空时绕过证书
     * @param password        https时选填 证书秘钥，为空时绕过证书
     * @return 请求结果
     */
    public static String doGet(String url, Map<String, String> headerMap, int timeout,
                               Charset responseCharset, boolean isHttps, String fileName,
                               String password) {
        String resultString = null;
        CloseableHttpResponse response = null;
        try (CloseableHttpClient httpclient = isHttps ? getHttpsClient(fileName, password) : HttpClients.createDefault()) {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (headerMap != null) {
                // 请求头参数拼进URI
                for (Map.Entry<String, String> elem : headerMap.entrySet()) {
                    builder.addParameter(elem.getKey(), elem.getValue());
                }
            }
            // 创建GET请求
            HttpGet httpGet = new HttpGet(builder.build());
            // 请求相关基础配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(timeout).setConnectTimeout(timeout)
                    .setConnectionRequestTimeout(timeout).build();
            httpGet.setConfig(defaultRequestConfig);
            // 执行请求
            response = httpclient.execute(httpGet);
            //有可能无响应
            if (response != null) {
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    if (responseCharset == null) {
                        responseCharset = StandardCharsets.UTF_8;
                    }
                    resultString = EntityUtils.toString(response.getEntity(), responseCharset);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
