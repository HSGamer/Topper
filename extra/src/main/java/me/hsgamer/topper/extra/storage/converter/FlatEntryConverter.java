package me.hsgamer.topper.extra.storage.converter;

public interface FlatEntryConverter<K, V> {
    K toKey(String key);

    String toRawKey(K key);

    V toValue(Object object);

    Object toRawValue(V object);
}
