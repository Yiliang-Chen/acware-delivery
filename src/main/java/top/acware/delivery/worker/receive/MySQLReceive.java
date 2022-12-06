package top.acware.delivery.worker.receive;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import top.acware.delivery.callback.Callback;
import top.acware.delivery.record.MySQLRecord;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class MySQLReceive implements Receive{

    private final Callback callback;
    private final BinaryLogClient client;
    private final Map<Long, HashMap<String, String>> dbMap = new HashMap<>();

    public MySQLReceive(Callback callback, Map<?, ?> config) {
        this.callback = callback;
        client = new BinaryLogClient((String) config.get("hostname"),
                Integer.parseInt(config.get("port").toString()),
                (String) config.get("username"),
                (String) config.get("password"));
        log.info("MySQL receive config: {}:{}, {}:{}", config.get("hostname"), config.get("port"), config.get("username"), config.get("password"));
        client.setServerId(Long.parseLong(config.get("server_id").toString()));
        client.registerEventListener(event -> {
            EventData data = event.getData();
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                if (!dbMap.containsKey(tableMapEventData.getTableId())) {
                    dbMap.put(tableMapEventData.getTableId(), new HashMap<String, String>(){{
                        put("database",tableMapEventData.getDatabase());
                        put("table", tableMapEventData.getTable());
                    }});
                }
            }
            if (data instanceof UpdateRowsEventData) {
                format(((UpdateRowsEventData) data).getTableId(), 0, ((UpdateRowsEventData) data).getRows());
            } else if (data instanceof WriteRowsEventData) {
                format(((WriteRowsEventData) data).getTableId(), 1, ((WriteRowsEventData) data).getRows());
            } else if (data instanceof DeleteRowsEventData) {
                format(((DeleteRowsEventData) data).getTableId(), -1, ((DeleteRowsEventData) data).getRows());
            }
        });
    }

    // 0 update, 1 insert, -1 delete
    public void format(Long tableId, int type,  List<?> data) {
        MySQLRecord records = new MySQLRecord(dbMap.get(tableId).get("database"), dbMap.get(tableId).get("table"));
        for (Object row : data) {
            records.setRecord(new MySQLRecord.UpdateRecord(
                    type == 0 ? Arrays.toString((Object[]) ((Map.Entry<?, ?>) row).getKey()) :
                            type == 1 ? null : Arrays.toString((Object[]) row),
                    type == 0 ? Arrays.toString((Object[]) ((Map.Entry<?, ?>) row).getValue()) :
                            type == 1 ? Arrays.toString((Object[]) row) : null));
        }
        callback.write(records);
    }

    @Override
    public void start() {
        try {
            client.connect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
