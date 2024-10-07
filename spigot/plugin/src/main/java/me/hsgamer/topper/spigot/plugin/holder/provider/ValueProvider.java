package me.hsgamer.topper.spigot.plugin.holder.provider;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ValueProvider {
    ValueProvider EMPTY = uuid -> CompletableFuture.completedFuture(Optional.empty());

    default Double getDefaultValue() {
        return 0D;
    }

    CompletableFuture<Optional<Double>> getValue(UUID uuid);
}
