package me.hsgamer.topper.spigot.plugin.config.converter;

import me.hsgamer.hscore.common.MapUtils;

import java.util.Collections;
import java.util.Map;

public class StringStringObjectMapConverter extends StringMapConverter<Map<String, Object>> {
    @Override
    protected Map<String, Object> toValue(Object value) {
        return MapUtils.castOptionalStringObjectMap(value).orElseGet(Collections::emptyMap);
    }

    @Override
    protected Object toRawValue(Object value) {
        return value;
    }
}
