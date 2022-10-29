package top.acware.delivery.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import top.acware.delivery.common.config.GlobalConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
public class HttpRequest {

    private static final CloseableHttpClient httpClient;
    private static final String CHARSET;
    private static final int TIMEOUT;

    static {
        CHARSET = GlobalConfig.getInstance().getString(GlobalConfig.HTTP_REQUEST_CHARSET);
        TIMEOUT = GlobalConfig.getInstance().getInt(GlobalConfig.HTTP_REQUEST_TIMEOUT);
        httpClient = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(
                        RequestConfig
                                .custom()
                                .setConnectionRequestTimeout(TIMEOUT)
                                .setConnectTimeout(TIMEOUT)
                                .setSocketTimeout(TIMEOUT)
                                .build()
                ).build();
    }

    public static String request(String url) {
        return request(url, RequestMethod.GET);
    }

    public static String request(String url, RequestMethod method) {
        return request(url, null, method);
    }

    public static String request(String url, Object datas, RequestMethod method) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "AcWare-Delivery-HttpRequest");
        return request(url, headers, datas, method);
    }

    public static String request(String url, Map<String, String> headers, Object data, RequestMethod method) {
        return request(url, headers, data, method, false);
    }

    public static String request(String url, Map<String, String> headers, Object datas, RequestMethod method, boolean toJson) {
        try {
            String data = null;
            if (toJson && datas != null)
                data = toJson(datas);
            else if (datas != null)
                data = datas.toString();
            HttpUriRequest req = getMethodRequest(url, method, data);
            log.info(" Request -> {} ", req);
            CloseableHttpResponse resp = httpClient.execute(setHeaders(req, headers));
            int code = resp.getStatusLine().getStatusCode();
            try {
                if (code != 200) {
                    log.error(" HTTP connection error, code = {} ", code);
                    throw new IOException(" HTTP connection error, code = " + code);
                }
                HttpEntity entity = resp.getEntity();
                String res = null;
                if (entity != null) {
                    res = EntityUtils.toString(entity, CHARSET);
                }
                log.info(" HTTP request success, response code -> {} ", code);
                return res;
            } finally {
                req.abort();
                resp.close();
            }
        } catch (IOException e) {
            log.error(" HTTP connection error ");
            throw new RuntimeException(e);
        }
    }

    private static HttpUriRequest setHeaders(HttpUriRequest req, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    req.setHeader(entry.getKey(), value);
                }
            }
        }
        return req;
    }

    private static HttpUriRequest getMethodRequest(String url, RequestMethod method, String data) {
        if (url.isEmpty()) {
            log.error(" URL is empty, please check. ");
            throw new NullPointerException(" URL is empty ");
        }
        try {
            switch (method) {
                case POST:
                    HttpPost req = new HttpPost(url);
                    if (data != null) {
                        req.setEntity(new StringEntity(data));
                    }
                    return req;
                case GET:
                    if (data != null) {
                        url += "?" + EntityUtils.toString(new StringEntity(data));
                    }
                    return new HttpGet(url);
                default:
                    log.warn(" [{}] method not support, change default method [GET] ", method);
                    return getMethodRequest(url, RequestMethod.GET, data);
            }
        } catch (UnsupportedEncodingException e) {
            log.error(" Charset unsupported -> {}", CHARSET);
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toJson(Object data) {
        if (data == null) {
            log.info(" Data is null or empty -> {} ", data);
            return null;
        }
        try {
            return new ObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public enum RequestMethod {
        GET, POST
    }

}
