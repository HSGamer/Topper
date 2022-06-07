package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.entry.DataEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Logger;

public abstract class DataHolder<T extends Comparable<T>> {
    public static final Logger LOGGER = Logger.getLogger(DataHolder.class.getName());
    private final Map<UUID, DataEntry<T>> entryMap = new ConcurrentHashMap<>();
    private final List<Consumer<DataEntry<T>>> removeListeners = new ArrayList<>();
    private final List<Consumer<DataEntry<T>>> createListeners = new ArrayList<>();
    private final List<Consumer<DataEntry<T>>> updateListeners = new ArrayList<>();
    private final String name;

    protected DataHolder(String name) {
        this.name = name;
    }

    public void onCreateEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public void onRemoveEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public void onUpdateEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public void onRegister() {
        // EMPTY
    }

    public void onUnregister() {
        // EMPTY
    }

    public T getDefaultValue() {
        return null;
    }

    public final void notifyCreateEntry(DataEntry<T> entry) {
        onCreateEntry(entry);
        createListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyRemoveEntry(DataEntry<T> entry) {
        onRemoveEntry(entry);
        removeListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyUpdateEntry(DataEntry<T> entry) {
        onUpdateEntry(entry);
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

    public final void register() {
        onRegister();
    }

    public final void unregister() {
        entryMap.values().forEach(this::notifyRemoveEntry);
        onUnregister();
        entryMap.clear();
        createListeners.clear();
        removeListeners.clear();
        updateListeners.clear();
    }

    public final String getName() {
        return name;
    }

    public final Map<UUID, DataEntry<T>> getEntryMap() {
        return entryMap;
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
}
