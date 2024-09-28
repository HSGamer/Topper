package me.hsgamer.topper.agent.storage.number;

import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;

public interface FlatNumberEntryConverter<K> extends FlatEntryConverter<K, Double> {
    @Override
    default Double toValue(Object object) {
        try {
            return Double.parseDouble(String.valueOf(object));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    default Object toRawValue(Double object) {
        return object;
    }
}
