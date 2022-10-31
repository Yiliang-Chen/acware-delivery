package top.acware.delivery.common.record;

import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * String 类型的数据
 */
@Setter
@ToString
@AllArgsConstructor
public class StringRecord implements Record{

    private String record;

    @Override
    public String getKey() {
        return record;
    }

    @Override
    public String getValue() {
        return record;
    }
}
