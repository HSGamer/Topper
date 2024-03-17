package me.hsgamer.topper.core.storage;

import me.hsgamer.topper.core.holder.DataHolder;

public interface DataStorageSupplier<T> {
    DataStorage<T> getStorage(DataHolder<T> dataHolder);

    default void enable() {
    }

    default void disable() {
    }
}
