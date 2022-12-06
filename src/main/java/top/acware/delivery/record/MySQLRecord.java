package top.acware.delivery.record;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySQLRecord implements Record{

    private String database;
    private String table;
    private final List<UpdateRecord> value = new ArrayList<>();

    public MySQLRecord(String database, String table) {
        this.database = database;
        this.table = table;
    }

    @Override
    public String toJsonString() {
        return "{\"database\":\"" + database + "\"," +
                "\"table\":" + table + "\"," +
                "\"value\":" + value + "\"}";
    }

    @Override
    public String toString() {
        return toJsonString();
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setRecord(UpdateRecord record) {
        value.add(record);
    }

    public void setRecords(List<UpdateRecord> records) {
        value.addAll(records);
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setTable(String table) {
        this.table = table;
    }


    public static class UpdateRecord {
        public String before;
        public String after;

        public UpdateRecord(String before, String after) {
            this.before = before;
            this.after = after;
        }

        @Override
        public String toString() {
            return "{\"before\":\"" + before + "\"," +
                    "\"after\":" + after + "\"}";
        }
    }

}
