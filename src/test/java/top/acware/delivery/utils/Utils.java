package top.acware.delivery.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    @Test
    public void properties() {
        System.out.println(new PropertiesTool().getProperties("delivery.properties").getProperty("callback.limit"));
    }

    @Test
    public void http() {
//        String url = "http://www.acware.top:9910/api/data/scroll";
//        String url = "http://www.acware.top:9910/api/data/get";
        String url = "https://www.baidu.com";
        Map<String, String> data = new HashMap<>();
        data.put("value", "0");
//        data.put("name", "city_list");
//        data.put("table", "city");
//        data.put("value", "count");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json;charset=UTF-8");
//        System.out.println(HttpRequest.request(url, headers, data, HttpRequest.RequestMethod.POST, true));
        System.out.println(HttpRequest.request(url));
    }

}
