package me.hsgamer.topper.placeholderleaderboard.provider;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ValueProvider {
    default Double getDefaultValue() {
        return 0D;
    }

    CompletableFuture<Optional<Double>> getValue(UUID uuid);
}
