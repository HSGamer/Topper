package me.hsgamer.topper.spigot.number;

import com.google.common.reflect.TypeToken;
import me.hsgamer.topper.spigot.config.DefaultConverterRegistry;
import me.hsgamer.topper.spigot.config.converter.StringMapConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class NumberConverterRegistry {
    private NumberConverterRegistry() {
        // EMPTY
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void register() {
        DefaultConverterRegistry.register(new TypeToken<Map<String, NumberFormatter>>() {
        }, new StringMapConverter<NumberFormatter>() {
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
        });
    }
}
