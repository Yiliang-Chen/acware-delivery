package top.acware.delivery.common.warning;

import top.acware.delivery.utils.HttpRequest;

import java.util.Map;

public class HttpWarning extends AbstractWarning{

    public Map<String, String> headers;
    public String url;
    public HttpRequest.RequestMethod method;
    public boolean toJson = false;

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void sendMessage() {
        if (url == null) {
            throw new NullPointerException(" URL is null, invoke setUrl method set ");
        }
        HttpRequest.request(url, headers, msg, method, toJson);
    }

    @Deprecated
    @Override
    public void setSubject(Object subject) {}

    public void setAndSendRequest(Object data) {
        super.setAndSendMessage(data);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMethod(HttpRequest.RequestMethod method) {
        this.method = method;
    }

    public void setToJson(boolean toJson) {
        this.toJson = toJson;
    }

    public void setData(Object data) {
        super.setMessage(data);
    }

}