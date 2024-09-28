package me.hsgamer.topper.spigot.plugin.config.converter;

import java.util.Objects;

public class StringValueMapConverter extends StringMapConverter<String> {
    @Override
    protected String toValue(Object value) {
        return Objects.toString(value, null);
    }

    @Override
    protected Object toRawValue(Object value) {
        return value;
    }
}
