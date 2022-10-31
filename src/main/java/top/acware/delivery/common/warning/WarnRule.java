package top.acware.delivery.common.warning;

import top.acware.delivery.common.record.Record;

/**
 * 告警规则
 */
public abstract class WarnRule {

    public abstract void rule(Record record);

}
