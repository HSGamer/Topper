package me.hsgamer.topper.agent.storage.simple.converter;

public interface FlatEntryConverter<K, V> {
    K toKey(String key);

    String toRawKey(K key);

    V toValue(Object object);

    Object toRawValue(V object);
}
