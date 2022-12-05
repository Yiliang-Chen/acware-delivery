package top.acware.delivery.warning;

import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.utils.HttpRequest;

import java.util.Map;

@Slf4j
public class HttpWarning extends AbstractWarning {

    private final String url;
    private final String method;
    private final Map<?, ?> headers;
    private final Map<?, ?> data;

    public HttpWarning(String url, String method, Map<?, ?> headers, Map<?, ?> data) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.data = data;
        execute();
    }

    @Override
    public void sendMessage() {
        String resp = HttpRequest.request(this.url, this.method, this.data, this.headers);
        log.info("Warning response: {}", resp);
    }
}
