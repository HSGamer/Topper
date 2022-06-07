package me.hsgamer.topper.core.common;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class DataEntry<T extends Comparable<T>> implements Comparable<DataEntry<T>> {
    private final UUID uuid;
    private final DataHolder<T> holder;
    private final AtomicReference<T> value;
    private final Set<EntryTempFlag> tempFlags = Collections.synchronizedSet(new HashSet<>());

    DataEntry(UUID uuid, DataHolder<T> holder) {
        this.uuid = uuid;
        this.holder = holder;
        this.value = new AtomicReference<>(holder.getDefaultValue());
    }

    public void save(boolean onUnregister) {
        if (hasFlag(EntryTempFlag.IS_SAVING)) return;
        if (!hasFlag(EntryTempFlag.NEED_SAVING)) return;
        removeFlag(EntryTempFlag.NEED_SAVING);
        addFlag(EntryTempFlag.IS_SAVING);
        holder.save(this, onUnregister).whenComplete((result, throwable) -> removeFlag(EntryTempFlag.IS_SAVING));
    }

    public void save() {
        save(false);
    }

    public UUID getUuid() {
        return uuid;
    }

    public T getValue() {
        return value.get();
    }

    public void setValue(T value) {
        this.value.set(value);
        addFlag(EntryTempFlag.NEED_SAVING);
    }

    public DataHolder<T> getHolder() {
        return holder;
    }

    public boolean hasFlag(EntryTempFlag flag) {
        return tempFlags.contains(flag);
    }

    public void addFlag(EntryTempFlag flag) {
        tempFlags.add(flag);
    }

    public void removeFlag(EntryTempFlag flag) {
        tempFlags.remove(flag);
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
