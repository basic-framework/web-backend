package com.zl.common.utils;


import com.alibaba.excel.util.StringUtils;
import com.zl.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

/**
 *  HttpUtils 工具类的实现，主要用来管理和关闭 Apache HttpClient 的连接池
 *  @Author GuihaoLv
 */
@Slf4j
public class HttpUtil {
    private static CloseableHttpClient httpClient; //是 Apache HttpClient 提供的客户端类，表示一个可以执行 HTTP 请求的对象，并且它支持关闭以释放资源。
    //用来管理 HTTP 连接池，它可以有效地复用连接，减少创建连接的开销，并且帮助管理连接池中的空闲连接。
    private static PoolingHttpClientConnectionManager connectionManager;

    static {
        connectionManager = new PoolingHttpClientConnectionManager();
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    public static void shutdown() {
        try {
            // 关闭连接池
            if (connectionManager != null) {
                connectionManager.shutdown();
            }
            // 关闭 HttpClient
            if (httpClient != null) {
                httpClient.close();
            }
            System.out.println("HttpClient 连接池和资源已关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



     /**
      * 向指定 URL 发送GET方法的请求
      *
      * @param url 发送请求的 URL
      * @return 所代表远程资源的响应结果
      */
     public static String sendGet(String url)
     {
         return sendGet(url, StringUtils.EMPTY);
     }

     /**
      * 向指定 URL 发送GET方法的请求
      *
      * @param url 发送请求的 URL
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @return 所代表远程资源的响应结果
      */
     public static String sendGet(String url, String param)
     {
         return sendGet(url, param, CommonConstant.UTF8);
     }


     /**
      * 向指定 URL 发送GET方法的请求
      *
      * @param url 发送请求的 URL
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @param contentType 编码类型
      * @return 所代表远程资源的响应结果
      */
     public static String sendGet(String url, String param, String contentType)
     {
         StringBuilder result = new StringBuilder();
         BufferedReader in = null;
         try
         {
             String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
             log.info("sendGet - {}", urlNameString);
             URI uri = new URI(urlNameString);
             URL realUrl = uri.toURL();
             URLConnection connection = realUrl.openConnection();
             connection.setRequestProperty("accept", "*/*");
             connection.setRequestProperty("connection", "Keep-Alive");
             connection.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
             connection.connect();
             in = new BufferedReader(new InputStreamReader(connection.getInputStream(), contentType));
             String line;
             while ((line = in.readLine()) != null)
             {
                 result.append(line);
             }
             log.info("recv - {}", result);
         }
         catch (ConnectException e)
         {
             log.error("调用HttpUtils.sendGet ConnectException, url=" + url + ",param=" + param, e);
         }
         catch (SocketTimeoutException e)
         {
             log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + url + ",param=" + param, e);
         }
         catch (IOException e)
         {
             log.error("调用HttpUtils.sendGet IOException, url=" + url + ",param=" + param, e);
         }
         catch (Exception e)
         {
             log.error("调用HttpsUtil.sendGet Exception, url=" + url + ",param=" + param, e);
         }
         finally
         {
             try
             {
                 if (in != null)
                 {
                     in.close();
                 }
             }
             catch (Exception ex)
             {
                 log.error("调用in.close Exception, url=" + url + ",param=" + param, ex);
             }
         }
         return result.toString();
     }
}