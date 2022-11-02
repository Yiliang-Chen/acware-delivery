package top.acware.delivery.common;

import lombok.ToString;
import org.junit.Test;
import top.acware.delivery.common.config.ConfigDef;
import top.acware.delivery.common.config.DefaultConfig;
import top.acware.delivery.common.warning.EmailWarning;
import top.acware.delivery.common.warning.HttpWarning;
import top.acware.delivery.utils.HttpRequest;
import top.acware.delivery.utils.ThreadPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Common {

    @Test
    public void test() {
        System.out.println(DefaultConfig.DeliveryConfig.CALLBACK_LIMIT);
    }

    @Test
    public void config() {
        System.out.println(DefaultConfig.getConfig().configKeys);
    }

    @Test
    public void email() {
        EmailWarning emailWarning = new EmailWarning();
        emailWarning.setSubject("AcWare Delivery 告警");
        emailWarning.addTo("18177410488@163.com");
        emailWarning.addCc("1982455737@qq.com");
        emailWarning.setMsg("AcWare Delivery 告警信息测试");
        ThreadPool.executor(emailWarning);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void http() {
        HttpWarning httpWarning = new HttpWarning();
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        httpWarning.setHeaders(header);
        Map<String, String> data = new HashMap<>();
        data.put("value", "0");
        httpWarning.setData(data);
        httpWarning.setMethod(HttpRequest.RequestMethod.POST);
        httpWarning.setToJson(true);
        httpWarning.setUrl("http://www.acware.top:9910/api/data/scroll");
        httpWarning.sendMessage();
    }

}
