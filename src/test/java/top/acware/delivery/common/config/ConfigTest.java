package top.acware.delivery.common.config;

public class ConfigTest {

    @org.junit.Test
    public void test() {
        System.out.println(GlobalConfig.getInstance().getInt(GlobalConfig.CALLBACK_LIMIT));
    }

}
