package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.storage.DataStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DataHolder<T extends Comparable<T>> {
    public static final Logger LOGGER = Logger.getLogger(DataHolder.class.getName());
    private final Map<UUID, DataEntry<T>> entryMap = new ConcurrentHashMap<>();
    private final List<Consumer<DataEntry<T>>> removeListeners = new ArrayList<>();
    private final List<Consumer<DataEntry<T>>> createListeners = new ArrayList<>();
    private final DataStorage<T> storage;
    private final String name;

    protected DataHolder(Function<DataHolder<T>, DataStorage<T>> storageSupplier, String name) {
        this.storage = storageSupplier.apply(this);
        this.name = name;
    }

    public void onCreateEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public void onRemoveEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public void onRegister() {
        // EMPTY
    }

    public void onPostRegister() {
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

    public final void addCreateListener(Consumer<DataEntry<T>> listener) {
        createListeners.add(listener);
    }

    public final void addRemoveListener(Consumer<DataEntry<T>> listener) {
        removeListeners.add(listener);
    }

    public final CompletableFuture<Void> save(DataEntry<T> entry, boolean onUnregister) {
        return storage.save(entry.getUuid(), entry.getValue(), onUnregister);
    }

    public final void register() {
        onRegister();
        storage.onRegister();
        storage.load()
                .whenComplete((entries, throwable) -> {
                    if (throwable != null) {
                        LOGGER.log(Level.SEVERE, "Failed to load top entries", throwable);
                    }
                    if (entries != null) {
                        entries.forEach((uuid, value) -> getOrCreateEntry(uuid).setValue(value));
                    }
                    onPostRegister();
                });
    }

    public final void unregister() {
        entryMap.values().forEach(entry -> {
            notifyRemoveEntry(entry);
            entry.save(true);
        });
        storage.onUnregister();
        onUnregister();
        entryMap.clear();
        createListeners.clear();
        removeListeners.clear();
    }

    public final String getName() {
        return name;
    }

    public final DataStorage<T> getStorage() {
        return storage;
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
