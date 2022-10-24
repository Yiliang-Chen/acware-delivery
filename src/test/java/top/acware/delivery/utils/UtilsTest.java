package top.acware.delivery.utils;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void properties() {
        System.out.println(new PropertiesTool().getProperties("server.properties").getProperty("callback.limit"));
    }

}
