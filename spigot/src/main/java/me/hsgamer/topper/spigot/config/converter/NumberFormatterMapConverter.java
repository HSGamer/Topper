package me.hsgamer.topper.spigot.config.converter;

import me.hsgamer.topper.spigot.formatter.NumberFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NumberFormatterMapConverter extends StringMapConverter<NumberFormatter> {
    @Override
    protected NumberFormatter toValue(Object value) {
        if (value instanceof Map) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                map.put(Objects.toString(entry.getKey()), entry.getValue());
            }
            return new NumberFormatter(map);
        }
        return null;
    }

    @Override
    protected Object toRawValue(Object value) {
        if (value instanceof NumberFormatter) {
            return ((NumberFormatter) value).toMap();
        }
        return null;
    }
}
