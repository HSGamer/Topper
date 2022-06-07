package me.hsgamer.topper.core.entry;

import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public final class DataEntry<T extends Comparable<T>> implements Comparable<DataEntry<T>> {
    private final UUID uuid;
    private final DataHolder<T> holder;
    private final AtomicReference<T> value;
    private final Map<EntryTempFlag, Boolean> tempFlags = new ConcurrentHashMap<>();

    public DataEntry(UUID uuid, DataHolder<T> holder) {
        this.uuid = uuid;
        this.holder = holder;
        this.value = new AtomicReference<>(holder.getDefaultValue());
    }

    public UUID getUuid() {
        return uuid;
    }

    public T getValue() {
        return value.get();
    }

    public void setValue(T value) {
        setValue(value, true);
    }

    public void setValue(T value, boolean notify) {
        this.value.set(value);
        if (notify) {
            holder.notifyUpdateEntry(this);
        }
    }

    public DataHolder<T> getHolder() {
        return holder;
    }

    public boolean hasFlag(EntryTempFlag flag) {
        return tempFlags.getOrDefault(flag, false);
    }

    public void addFlag(EntryTempFlag flag) {
        tempFlags.put(flag, true);
    }

    public void removeFlag(EntryTempFlag flag) {
        tempFlags.put(flag, false);
    }

    @Override
    public int compareTo(DataEntry<T> o) {
        T v1 = value.get();
        T v2 = o.value.get();
        if (v1 == null) {
            return v2 == null ? 0 : 1;
        }
        if (v2 == null) return -1;
        return v1.compareTo(v2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataEntry)) return false;
        DataEntry<?> dataEntry = (DataEntry<?>) o;
        return getUuid().equals(dataEntry.getUuid()) && getHolder().equals(dataEntry.getHolder()) && getValue().equals(dataEntry.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid(), getHolder(), getValue());
    }
}
