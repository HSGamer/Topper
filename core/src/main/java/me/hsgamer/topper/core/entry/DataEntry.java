package me.hsgamer.topper.core.entry;

import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public final class DataEntry<T> {
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

    public void setValue(UnaryOperator<T> operator) {
        setValue(operator, true);
    }

    public void setValue(T value, boolean notify) {
        if (Objects.equals(this.value.get(), value)) return;
        this.value.set(value);
        if (notify) holder.getUpdateListenerManager().notifyListeners(this);
    }

    public void setValue(UnaryOperator<T> operator, boolean notify) {
        setValue(operator.apply(value.get()), notify);
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
