package me.hsgamer.topper.core.agent.snapshot;

import java.util.UUID;

public class DataSnapshot<T> {
    public final UUID uuid;
    public final T value;

    DataSnapshot(UUID uuid, T value) {
        this.uuid = uuid;
        this.value = value;
    }
}
