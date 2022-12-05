package top.acware.delivery.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import top.acware.delivery.exception.WarningException;
import top.acware.delivery.record.Record;

import java.util.List;
import java.util.Map;

@Slf4j
public class WarningRule {

    private final Map<String, JexlExpression> expression;
    private final Map<String, List<Map<?, ?>>> executor;
    private final Map<String, Map<?, ?>> global;
    private final MapContext context = new MapContext();

    public WarningRule(Map<String, JexlExpression> expression, Map<String, List<Map<?, ?>>> executor, Map<String, Map<?, ?>> global) {
        this.expression = expression;
        this.executor = executor;
        this.global = global;
    }

    public void checkRecord(Record record) {
        for (String module : expression.keySet()) {
            context.set("record", record);
            if (!(boolean) expression.get(module).evaluate(context)) {
                log.info("Warn rule [{}] trigger {}", module, record);
                sendMessage(module, record);
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void sendMessage(String modules, Record record) {
        List<Map<?, ?>> list = executor.get(modules);
        for (Map<?, ?> config : list) {
            String pattern = (String) config.get("pattern");
            switch (pattern) {
                case "email": {
                    new EmailWarning(global.get("email"), (String) config.get("smtp.subject"),
                            ((String) config.get("smtp.to")).split(","),
                            ((String) config.get("smtp.cc")).split(","),
                            String.format((String) config.get("warn.context"), record));
                    break;
                }
                case "http": {
                    Map<Object, Object> data = (Map<Object, Object>) config.get("data");
                    data.put("content", String.format((String) data.get("content"), record));
                    new HttpWarning((String) config.get("url"),
                            (String) config.get("method"),
                            (Map<?, ?>) config.get("headers"),
                            data);
                    break;
                }
                default: {
                    log.error("Executor method [{}] not support", pattern);
                    throw new WarningException(String.format("Executor method [%s] not support", pattern));
                }
            }
            log.info("Send warning message = {}", config);
        }
    }

}
