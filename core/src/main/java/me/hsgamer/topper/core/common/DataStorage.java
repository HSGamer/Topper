package me.hsgamer.topper.core.common;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public interface DataStorage<T extends Comparable<T>> {
    Logger LOGGER = Logger.getLogger(DataStorage.class.getName());

    CompletableFuture<Map<UUID, T>> load(DataHolder<T> holder);

    CompletableFuture<Void> save(DataEntry<T> dataEntry, boolean onUnregister);

    default void onRegister(DataHolder<T> holder) {
        // EMPTY
    }

    default void onUnregister(DataHolder<T> holder) {
        // EMPTY
    }
}
