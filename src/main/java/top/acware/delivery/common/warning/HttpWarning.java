package top.acware.delivery.common.warning;

import top.acware.delivery.utils.HttpRequest;

import java.util.Map;

/**
 * 调用 Http 接口发送告警
 */
public class HttpWarning extends AbstractWarning{

    public Map<String, String> headers;
    public Object data;
    public String url;
    public HttpRequest.RequestMethod method;
    public boolean toJson = false;

    // 设置 headers
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void sendMessage() {
        if (url == null) {
            throw new NullPointerException(" URL is null, invoke setUrl method set ");
        }
        if (method == null) {
            method = HttpRequest.RequestMethod.GET;
        }
        HttpRequest.request(url, headers, data, method, toJson);
    }

    // 设置 URL
    public void setUrl(String url) {
        this.url = url;
    }

    // 设置请求方法
    public void setMethod(HttpRequest.RequestMethod method) {
        this.method = method;
    }

    // 请求数据是否转成 JSON 格式
    public void setToJson(boolean toJson) {
        this.toJson = toJson;
    }

    // 设置请求数据
    public void setData(Object data) {
        this.data = data;
    }

}
