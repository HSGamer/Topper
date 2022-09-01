package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.entry.DataEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DataHolder<T> {
    private final Map<UUID, DataEntry<T>> entryMap = new ConcurrentHashMap<>();
    private final List<Consumer<DataEntry<T>>> removeListeners = new ArrayList<>();
    private final List<Consumer<DataEntry<T>>> createListeners = new ArrayList<>();
    private final List<Consumer<DataEntry<T>>> updateListeners = new ArrayList<>();
    private final List<Runnable> registerListeners = new ArrayList<>();
    private final List<Runnable> beforeUnregisterListeners = new ArrayList<>();
    private final List<Runnable> unregisterListeners = new ArrayList<>();
    private final String name;

    protected DataHolder(String name) {
        this.name = name;
    }

    public T getDefaultValue() {
        return null;
    }

    public final void notifyCreateEntry(DataEntry<T> entry) {
        createListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyRemoveEntry(DataEntry<T> entry) {
        removeListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyUpdateEntry(DataEntry<T> entry) {
        updateListeners.forEach(listener -> listener.accept(entry));
    }

    public final void addCreateListener(Consumer<DataEntry<T>> listener) {
        createListeners.add(listener);
    }

    public final void addRemoveListener(Consumer<DataEntry<T>> listener) {
        removeListeners.add(listener);
    }

    public final void addUpdateListener(Consumer<DataEntry<T>> listener) {
        updateListeners.add(listener);
    }

    public final void addRegisterListener(Runnable listener) {
        registerListeners.add(listener);
    }

    public final void addBeforeUnregisterListener(Runnable listener) {
        beforeUnregisterListeners.add(listener);
    }

    public final void addUnregisterListener(Runnable listener) {
        unregisterListeners.add(listener);
    }

    public final void register() {
        registerListeners.forEach(Runnable::run);
    }

    public final void unregister() {
        beforeUnregisterListeners.forEach(Runnable::run);

        entryMap.values().forEach(this::notifyRemoveEntry);
        entryMap.clear();

        unregisterListeners.forEach(Runnable::run);

        createListeners.clear();
        removeListeners.clear();
        updateListeners.clear();
        registerListeners.clear();
        beforeUnregisterListeners.clear();
        unregisterListeners.clear();
    }

    public final String getName() {
        return name;
    }

    public final Map<UUID, DataEntry<T>> getEntryMap() {
        return Collections.unmodifiableMap(entryMap);
    }

    public final Optional<DataEntry<T>> getEntry(UUID uuid) {
        return Optional.ofNullable(entryMap.get(uuid));
    }

    public final DataEntry<T> getOrCreateEntry(UUID uuid) {
        return entryMap.computeIfAbsent(uuid, u -> {
            DataEntry<T> entry = new DataEntry<>(u, this);
            notifyCreateEntry(entry);
            return entry;
        });
    }

    public final void removeEntry(UUID uuid) {
        Optional.ofNullable(entryMap.remove(uuid)).ifPresent(this::notifyRemoveEntry);
    }
}
