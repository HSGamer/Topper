package me.hsgamer.topper.core.top;

import java.util.UUID;

public class TopSnapshot<T extends Comparable<T>> {
    public final UUID uuid;
    public final T value;

    TopSnapshot(UUID uuid, T value) {
        this.uuid = uuid;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public T getValue() {
        return value;
    }
}
