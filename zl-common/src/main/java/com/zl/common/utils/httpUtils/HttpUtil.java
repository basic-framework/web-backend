package com.zl.common.utils.httpUtils;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.zl.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

 /**
 *  HttpUtils 工具类的实现，提供各种HTTP请求方法（GET、POST、PUT、DELETE、PATCH）
 *  使用Apache HttpClient连接池管理，支持JSON格式请求和响应
 *  @Author GuihaoLv
 */
@Slf4j
public class HttpUtil {
    private static CloseableHttpClient httpClient; //是 Apache HttpClient 提供的客户端类，表示一个可以执行 HTTP 请求的对象，并且它支持关闭以释放资源。
    //用来管理 HTTP 连接池，它可以有效地复用连接，减少创建连接的开销，并且帮助管理连接池中的空闲连接。
    private static PoolingHttpClientConnectionManager connectionManager;
    //请求配置，设置连接超时、读取超时等
    private static RequestConfig requestConfig;

    static {
        // 配置连接池
        connectionManager = new PoolingHttpClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(200);
        // 设置每个路由的默认连接数
        connectionManager.setDefaultMaxPerRoute(20);
        
        // 配置请求参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(15000)  // 设置读取超时时间
                .setConnectTimeout(5000)   // 设置连接超时时间
                .setConnectionRequestTimeout(3000) // 设置从连接池获取连接的超时时间
                .build();
                
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
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
         String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
         return executeGet(urlNameString, null);
     }

     /**
      * 向指定 URL 发送GET方法的请求
      *
      * @param url 发送请求的 URL
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendGet(String url, Map<String, String> headers)
     {
         return executeGet(url, headers);
     }

     /**
      * 向指定 URL 发送GET方法的请求
      *
      * @param url 发送请求的 URL
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendGet(String url, String param, Map<String, String> headers)
     {
         String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
         return executeGet(urlNameString, headers);
     }

     /**
      * 向指定 URL 发送POST方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @return 所代表远程资源的响应结果
      */
     public static String sendPost(String url, String jsonParam)
     {
         return sendPost(url, jsonParam, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送POST方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPost(String url, String jsonParam, Map<String, String> headers)
     {
         return executePost(url, jsonParam, headers);
     }

     /**
      * 向指定 URL 发送POST方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @return 所代表远程资源的响应结果
      */
     public static String sendPost(String url, Object param)
     {
         return sendPost(url, param, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送POST方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPost(String url, Object param, Map<String, String> headers)
     {
         String jsonParam = JSON.toJSONString(param);
         return executePost(url, jsonParam, headers);
     }

     /**
      * 向指定 URL 发送PUT方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @return 所代表远程资源的响应结果
      */
     public static String sendPut(String url, String jsonParam)
     {
         return sendPut(url, jsonParam, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送PUT方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPut(String url, String jsonParam, Map<String, String> headers)
     {
         return executePut(url, jsonParam, headers);
     }

     /**
      * 向指定 URL 发送PUT方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @return 所代表远程资源的响应结果
      */
     public static String sendPut(String url, Object param)
     {
         return sendPut(url, param, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送PUT方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPut(String url, Object param, Map<String, String> headers)
     {
         String jsonParam = JSON.toJSONString(param);
         return executePut(url, jsonParam, headers);
     }

     /**
      * 向指定 URL 发送DELETE方法的请求
      *
      * @param url 发送请求的 URL
      * @return 所代表远程资源的响应结果
      */
     public static String sendDelete(String url)
     {
         return sendDelete(url, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送DELETE方法的请求
      *
      * @param url 发送请求的 URL
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendDelete(String url, Map<String, String> headers)
     {
         return executeDelete(url, headers);
     }

     /**
      * 向指定 URL 发送DELETE方法的请求（带参数）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @return 所代表远程资源的响应结果
      */
     public static String sendDelete(String url, String param)
     {
         return sendDelete(url, param, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送DELETE方法的请求（带参数）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendDelete(String url, String param, Map<String, String> headers)
     {
         String urlNameString = StringUtils.isNotBlank(param) ? url + "?" + param : url;
         return executeDelete(urlNameString, headers);
     }

     /**
      * 向指定 URL 发送PATCH方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @return 所代表远程资源的响应结果
      */
     public static String sendPatch(String url, String jsonParam)
     {
         return sendPatch(url, jsonParam, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送PATCH方法的请求（JSON格式）
      *
      * @param url 发送请求的 URL
      * @param jsonParam JSON格式的请求参数
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPatch(String url, String jsonParam, Map<String, String> headers)
     {
         return executePatch(url, jsonParam, headers);
     }

     /**
      * 向指定 URL 发送PATCH方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @return 所代表远程资源的响应结果
      */
     public static String sendPatch(String url, Object param)
     {
         return sendPatch(url, param, (Map<String, String>) null);
     }

     /**
      * 向指定 URL 发送PATCH方法的请求（对象自动转JSON）
      *
      * @param url 发送请求的 URL
      * @param param 请求参数对象
      * @param headers 请求头信息
      * @return 所代表远程资源的响应结果
      */
     public static String sendPatch(String url, Object param, Map<String, String> headers)
     {
         String jsonParam = JSON.toJSONString(param);
         return executePatch(url, jsonParam, headers);
     }

     /**
      * 执行GET请求
      */
     private static String executeGet(String url, Map<String, String> headers)
     {
         HttpGet httpGet = new HttpGet(url);
         setHeaders(httpGet, headers);
         return executeRequest(httpGet, url);
     }

     /**
      * 执行POST请求
      */
     private static String executePost(String url, String jsonParam, Map<String, String> headers)
     {
         HttpPost httpPost = new HttpPost(url);
         setHeaders(httpPost, headers);
         if (StringUtils.isNotBlank(jsonParam)) {
             StringEntity entity = new StringEntity(jsonParam, StandardCharsets.UTF_8);
             entity.setContentType("application/json");
             httpPost.setEntity(entity);
         }
         return executeRequest(httpPost, url);
     }

     /**
      * 执行PUT请求
      */
     private static String executePut(String url, String jsonParam, Map<String, String> headers)
     {
         HttpPut httpPut = new HttpPut(url);
         setHeaders(httpPut, headers);
         if (StringUtils.isNotBlank(jsonParam)) {
             StringEntity entity = new StringEntity(jsonParam, StandardCharsets.UTF_8);
             entity.setContentType("application/json");
             httpPut.setEntity(entity);
         }
         return executeRequest(httpPut, url);
     }

     /**
      * 执行DELETE请求
      */
     private static String executeDelete(String url, Map<String, String> headers)
     {
         HttpDelete httpDelete = new HttpDelete(url);
         setHeaders(httpDelete, headers);
         return executeRequest(httpDelete, url);
     }

     /**
      * 执行PATCH请求
      */
     private static String executePatch(String url, String jsonParam, Map<String, String> headers)
     {
         HttpPatch httpPatch = new HttpPatch(url);
         setHeaders(httpPatch, headers);
         if (StringUtils.isNotBlank(jsonParam)) {
             StringEntity entity = new StringEntity(jsonParam, StandardCharsets.UTF_8);
             entity.setContentType("application/json");
             httpPatch.setEntity(entity);
         }
         return executeRequest(httpPatch, url);
     }

     /**
      * 设置请求头
      */
     private static void setHeaders(HttpRequestBase request, Map<String, String> headers)
     {
         // 设置默认请求头
         request.setHeader("accept", "*/*");
         request.setHeader("connection", "Keep-Alive");
         request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
         
         // 设置自定义请求头
         if (headers != null && !headers.isEmpty()) {
             for (Map.Entry<String, String> entry : headers.entrySet()) {
                 request.setHeader(entry.getKey(), entry.getValue());
             }
         }
     }

     /**
      * 执行HTTP请求
      */
     private static String executeRequest(HttpRequestBase request, String url)
     {
         CloseableHttpResponse response = null;
         try {
             log.info("sendRequest - {}", url);
             response = httpClient.execute(request);
             HttpEntity entity = response.getEntity();
             String result = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
             log.info("recv - {}", result);
             EntityUtils.consume(entity);
             return result;
         } catch (Exception e) {
             log.error("调用HttpUtils.{} Exception, url={}", request.getMethod(), url, e);
             return "";
         } finally {
             try {
                 if (response != null) {
                     response.close();
                 }
             } catch (IOException e) {
                 log.error("关闭response异常", e);
             }
         }
     }

     /**
      * URL编码
      */
     public static String encodeUrl(String url) {
         try {
             return URLEncoder.encode(url, StandardCharsets.UTF_8.name());
         } catch (UnsupportedEncodingException e) {
             log.error("URL编码异常", e);
             return url;
         }
     }
 }