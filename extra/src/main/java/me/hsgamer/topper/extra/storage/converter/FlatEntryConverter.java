package me.hsgamer.topper.extra.storage.converter;

public interface FlatEntryConverter<T> {
    T toValue(Object object);

    Object toRaw(T object);
}
