package me.hsgamer.topper.core;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public final class TopEntry implements Comparable<TopEntry> {
    private final UUID uuid;
    private final TopHolder topHolder;
    private final AtomicReference<Double> value = new AtomicReference<>(0D);
    private final AtomicBoolean needSaving = new AtomicBoolean(false);
    private final AtomicBoolean isSaving = new AtomicBoolean(false);

    TopEntry(UUID uuid, TopHolder topHolder) {
        this.uuid = uuid;
        this.topHolder = topHolder;
    }

    public void update() {
        topHolder.updateNewValue(uuid).thenAccept(optional -> {
            if (optional.isPresent()) {
                double currentValue = value.get();
                double newValue = optional.get();
                if (currentValue != newValue) {
                    value.set(newValue);
                    needSaving.set(true);
                    topHolder.notifyUpdateEntry(this);
                }
            }
        });
    }

    public void save(boolean onUnregister) {
        if (isSaving.get()) return;
        if (!needSaving.get()) return;
        needSaving.set(false);
        isSaving.set(true);
        topHolder.save(this, onUnregister).whenComplete((result, throwable) -> isSaving.set(false));
    }

    public void save() {
        save(false);
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getValue() {
        return value.get();
    }

    void setValue(double value) {
        this.value.set(value);
    }

    public TopHolder getTopHolder() {
        return topHolder;
    }

    @Override
    public int compareTo(TopEntry o) {
        int compare = Double.compare(value.get(), o.value.get());
        if (compare == 0) {
            compare = getUuid().compareTo(o.getUuid());
        }
        return compare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopEntry topEntry = (TopEntry) o;
        return uuid.equals(topEntry.uuid) && topHolder.equals(topEntry.topHolder) && value.equals(topEntry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, topHolder, value);
    }
}
