package top.acware.delivery.test.worker.receive;

import top.acware.delivery.callback.CacheCallback;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.utils.ThreadPool;
import top.acware.delivery.worker.receive.MySQLReceive;
import top.acware.delivery.worker.receive.Receive;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MySQLReceiveTest {

    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>();
        map.put("hostname", "localhost");
        map.put("port", 3306);
        map.put("username", "root");
        map.put("password", "123456");
        map.put("server_id", 1);
        Callback callback = new CacheCallback(3L);
        Receive mysql = new MySQLReceive(callback, map);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mysql.start();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (callback.canRead()) {
                        System.out.println(callback.read());
                    }
                }
            }
        }).start();
    }

}
