package me.hsgamer.topper.core;

import java.util.UUID;

public class TopSnapshot {
    public final UUID uuid;
    public final double value;

    TopSnapshot(UUID uuid, double value) {
        this.uuid = uuid;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getValue() {
        return value;
    }
}
