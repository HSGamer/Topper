package me.hsgamer.topper.core;

import java.math.BigDecimal;
import java.util.UUID;

public class TopSnapshot {
    public final UUID uuid;
    public final BigDecimal value;

    TopSnapshot(UUID uuid, BigDecimal value) {
        this.uuid = uuid;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public BigDecimal getValue() {
        return value;
    }
}
