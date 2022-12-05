package top.acware.delivery.config.warning;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.internal.Engine;
import top.acware.delivery.warning.WarningRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WarningConfig {

    public final WarningRule warningRule;

    public WarningConfig(Map<?, ?> config) {
        Map<String, JexlExpression> expression = new HashMap<>();
        Map<String, List<Map<?, ?>>> executor = new HashMap<>();
        Map<String, Map<?, ?>> global = new HashMap<>();

        setGlobal((Map<?, ?>) config.get("global"), global);
        Map<?, ?> rule = (Map<?, ?>) config.get("rule");
        for (String module_field : ((String) rule.get("module")).split(",")) {
            Map<?, ?> rule_module = (Map<?, ?>) rule.get(module_field);
            expression.put(module_field, new Engine().createExpression((String) rule_module.get("expression")));
            parseExecutor(module_field, (Map<?, ?>) rule_module.get("executor"), executor);
        }

        warningRule = new WarningRule(expression, executor, global);
        log.info("Warning rule config: expression -> {}, executor -> {}", expression, executor);
    }

    private void parseExecutor(String module_field, Map<?,?> config, Map<String, List<Map<?, ?>>> executor) {
        List<Map<?, ?>> list = new ArrayList<>();
        for (String executor_module : ((String) config.get("module")).split(",")) {
            list.add((Map<?, ?>) config.get(executor_module));
        }
        executor.put(module_field, list);
    }

    private void setGlobal(Map<?,?> config, Map<String, Map<?, ?>> global) {
        if (config.containsKey("email"))
            global.put("email", (Map<?, ?>) config.get("email"));
    }

}
