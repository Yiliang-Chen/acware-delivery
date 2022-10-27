package top.acware.delivery.common;

import org.junit.Test;
import top.acware.delivery.common.config.GlobalConfig;
import top.acware.delivery.common.warning.EmailWarning;
import top.acware.delivery.utils.ThreadPool;

import java.io.IOException;

public class CommonTest {

    @Test
    public void test() {
        System.out.println(GlobalConfig.getInstance().getInt(GlobalConfig.CALLBACK_LIMIT));
    }

    @Test
    public void email() {
        EmailWarning emailWarning = new EmailWarning();
        emailWarning.setSubject("AcWare Delivery 告警");
        emailWarning.addTo("18177410488@163.com");
        emailWarning.addCc("1982455737@qq.com");
        emailWarning.setMessage("AcWare Delivery 告警信息测试");
        ThreadPool.getExecutor().execute(emailWarning);
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
