package top.acware.delivery.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import top.acware.delivery.config.global.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Http 请求工具类
 */
@Slf4j
public class HttpRequest {

    private static final CloseableHttpClient httpClient;

    static {
        httpClient = HttpClientBuilder.create().build();
    }

    public static String request(String url, String method, Map<?, ?> data, Map<?, ?> headers) {
        try {
            HttpUriRequest req = getMethodRequest(url, method, data);
            log.info("Request -> {}", req);
            CloseableHttpResponse resp = httpClient.execute(setHeaders(req, headers));
            int code = resp.getStatusLine().getStatusCode();
            try {
                if (code != 200) {
                    log.error("HTTP connection error, code = {} ", code);
                }
                HttpEntity entity = resp.getEntity();
                String res = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
                log.info("HTTP request success, response code -> {} ", code);
                return res;
            } finally {
                req.abort();
                resp.close();
            }
        } catch (IOException e) {
            log.error("HTTP connection error");
        }
        return null;
    }

    private static HttpUriRequest setHeaders(HttpUriRequest req, Map<?, ?> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<?, ?> entry : headers.entrySet()) {
                req.setHeader((String) entry.getKey(), (String) entry.getValue());
            }
        }
        return req;
    }

    private static HttpUriRequest getMethodRequest(String url, String method, Map<?, ?> data) {
        if (url.isEmpty()) {
            log.error("URL is empty, please check.");
            throw new NullPointerException("URL is empty");
        }
        method = method.toUpperCase();
        switch (method) {
            case "POST": {
                HttpPost req = new HttpPost(url);
                if (data != null && !data.isEmpty()) {
                    try {
                        req.setEntity(new StringEntity(Constants.MAPPER.writeValueAsString(data), StandardCharsets.UTF_8));
                    } catch (JsonProcessingException e) {
                        log.error("Data parse exception: {}", data);
                    }
                }
                return req;
            }
            case "GET": {
                try {
                    if (data != null) {
                        url += "?properties=" + URLEncoder.encode(data.toString(), "UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                return new HttpGet(url);
            }
            default: {
                log.warn("[{}] method not support, change [GET] method", method);
                return getMethodRequest(url, "GET", data);
            }
        }
    }

}
