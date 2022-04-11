package me.hsgamer.topper.core;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TopStorage extends Initializer {
    CompletableFuture<Map<UUID, BigDecimal>> load(String name);

    void save(TopEntry topEntry, String name, boolean onUnregister);
}
