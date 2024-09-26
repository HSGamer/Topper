package me.hsgamer.topper.spigot.config.converter;

import me.hsgamer.hscore.config.annotation.converter.Converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class StringMapConverter<T> implements Converter {
    protected abstract T toValue(Object value);

    protected abstract Object toRawValue(Object value);

    @Override
    public Map<String, T> convert(Object raw) {
        if (raw instanceof Map) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) raw).entrySet()) {
                map.put(Objects.toString(entry.getKey()), entry.getValue());
            }
            Map<String, T> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                T resultValue = toValue(entry.getValue());
                if (resultValue != null) {
                    result.put(entry.getKey(), resultValue);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public Map<String, Object> convertToRaw(Object value) {
        if (value instanceof Map) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                map.put(Objects.toString(entry.getKey()), entry.getValue());
            }
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object rawValue = toRawValue(entry.getValue());
                if (rawValue != null) {
                    result.put(entry.getKey(), rawValue);
                }
            }
            return result;
        }
        return null;
    }
}
