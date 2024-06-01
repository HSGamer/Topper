package me.hsgamer.topper.core.storage;

import me.hsgamer.topper.core.holder.DataHolder;

public interface DataStorageSupplier<K, V> {
    DataStorage<K, V> getStorage(DataHolder<K, V> dataHolder);

    default void enable() {
    }

    default void disable() {
    }
}
