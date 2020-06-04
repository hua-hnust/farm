package com.farm.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author xhua
 * @Date 2020/3/26 14:46
 **/
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /** 连接超时时间 **/
    private static final int DEFAULT_CONNECT_TIMEOUT = 6000;
    /** 服务端处理超时时间 **/
    private static final int DEFAULT_SOCKET_TIMEOUT = 15000;

    /**
     * http get 请求
     * @return
     */
    public static String get(String url){
        return get(url, null);
    }

    public static String get(String url, String responseCharset) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setConfig(RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build());

            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity responseEntity = response.getEntity();
            if ( responseEntity != null ){
                String result = responseCharset != null ? EntityUtils.toString(responseEntity, responseCharset) : EntityUtils.toString(responseEntity);
                logger.debug("get Response[{}] : {}", url, result);
                return result;
            } else {
                logger.debug("get Response[{}] : null", url);
            }
        } catch (Exception e){
            logger.error("Send get error : " + url, e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * post 请求
     * @param url
     * @param params
     * @return
     */
    public static String postJson(String url, Object params){
        return postJson(url, params, null);
    }

    /**
     * 指定返回值编码
     * @param url       请求链接
     * @param params    body 实体
     * @param headers   请求头参数
     * @return
     */
    public static String postJson(String url, Object params, Map<String, String> headers){
        return postJson(url, params, headers, null);
    }

    /**
     * 指定返回值编码
     * @param url       请求链接
     * @param params    body 实体
     * @param headers   请求头参数
     * @return
     */
    public static String postJson(String url, Object params, Map<String, String> headers, String responseCharset){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String requestParams = JSON.toJSONString(params);
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setConfig(RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT).setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build());

            HttpEntity paramsEntity = new StringEntity(requestParams, ContentType.APPLICATION_JSON);
            httpPost.setEntity(paramsEntity);

            if ( headers != null ){
                Iterator<String> keys = headers.keySet().iterator();
                while ( keys.hasNext() ){
                    String key = keys.next();
                    httpPost.addHeader(key, headers.get(key));
                }
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if ( responseEntity != null ){
                String result = responseCharset != null ? EntityUtils.toString(responseEntity, responseCharset) : EntityUtils.toString(responseEntity);
                logger.debug("postJson Response[{}] : {} , {}", url, requestParams, result);
                return result;
            } else {
                logger.debug("Post Response[{}] : {} , null", url, requestParams);
            }
        } catch (Exception e){
            logger.error("Send post error : " + url + " , " + requestParams, e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
