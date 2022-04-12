package me.hsgamer.topper.core;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public interface TopStorage {
    Logger LOGGER = Logger.getLogger(TopStorage.class.getName());

    CompletableFuture<Map<UUID, BigDecimal>> load(TopHolder holder);

    CompletableFuture<Void> save(TopEntry topEntry, boolean onUnregister);

    default void onRegister(TopHolder holder) {
        // EMPTY
    }

    default void onUnregister(TopHolder holder) {
        // EMPTY
    }
}
