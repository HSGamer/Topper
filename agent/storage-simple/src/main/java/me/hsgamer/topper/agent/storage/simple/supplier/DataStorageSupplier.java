package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.topper.agent.storage.DataStorage;
import me.hsgamer.topper.core.DataHolder;

public interface DataStorageSupplier<K, V> {
    DataStorage<K, V> getStorage(DataHolder<K, V> dataHolder);

    default void enable() {
    }

    default void disable() {
    }
}
