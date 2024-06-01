package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.listener.EventState;
import me.hsgamer.topper.core.listener.ListenerManager;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder<K, V> {
    private final Map<K, DataEntry<K, V>> entryMap = new ConcurrentHashMap<>();
    private final ListenerManager<K, V> listenerManager = new ListenerManager<>();
    private final String name;

    protected DataHolder(String name) {
        this.name = name;
    }

    public V getDefaultValue() {
        return null;
    }

    public ListenerManager<K, V> getListenerManager() {
        return listenerManager;
    }

    public final void register() {
        listenerManager.call(EventStates.REGISTER);
    }

    public final void unregister() {
        listenerManager.call(EventStates.BEFORE_UNREGISTER);

        entryMap.values().forEach(entry -> getListenerManager().call(EventStates.UNREGISTER, entry));
        entryMap.clear();

        listenerManager.call(EventStates.UNREGISTER);

        listenerManager.clear();
    }

    public final String getName() {
        return name;
    }

    public final Map<K, DataEntry<K, V>> getEntryMap() {
        return Collections.unmodifiableMap(entryMap);
    }

    public final Optional<DataEntry<K, V>> getEntry(K key) {
        return Optional.ofNullable(entryMap.get(key));
    }

    public final DataEntry<K, V> getOrCreateEntry(K key) {
        return entryMap.computeIfAbsent(key, u -> {
            DataEntry<K, V> entry = new DataEntry<>(u, this);
            listenerManager.call(EventStates.CREATE, entry);
            return entry;
        });
    }

    public final void removeEntry(K key) {
        Optional.ofNullable(entryMap.remove(key)).ifPresent(entry -> listenerManager.call(EventStates.REMOVE, entry));
    }

    public enum EventStates implements EventState {
        CREATE,
        REMOVE,
        UPDATE,
        REGISTER,
        BEFORE_UNREGISTER,
        UNREGISTER
    }
}
