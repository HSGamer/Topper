package me.hsgamer.topper.core.entry;

import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

public final class DataEntry<K, V> {
    private final K key;
    private final DataHolder<K, V> holder;
    private final AtomicReference<V> value;
    private final Map<EntryTempFlag, Boolean> tempFlags = new ConcurrentHashMap<>();

    public DataEntry(K key, DataHolder<K, V> holder) {
        this.key = key;
        this.holder = holder;
        this.value = new AtomicReference<>(holder.getDefaultValue());
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value.get();
    }

    public void setValue(V value) {
        setValue(value, true);
    }

    public void setValue(UnaryOperator<V> operator) {
        setValue(operator, true);
    }

    public void setValue(V value, boolean notify) {
        if (Objects.equals(this.value.get(), value)) return;
        this.value.set(value);
        if (notify) holder.getListenerManager().call(DataHolder.EventStates.UPDATE, this);
    }

    public void setValue(UnaryOperator<V> operator, boolean notify) {
        setValue(operator.apply(value.get()), notify);
    }

    public DataHolder<K, V> getHolder() {
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
        if (o == null || getClass() != o.getClass()) return false;
        DataEntry<?, ?> dataEntry = (DataEntry<?, ?>) o;
        return Objects.equals(getKey(), dataEntry.getKey()) && Objects.equals(getHolder(), dataEntry.getHolder()) && Objects.equals(getValue(), dataEntry.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getHolder(), getValue());
    }
}
