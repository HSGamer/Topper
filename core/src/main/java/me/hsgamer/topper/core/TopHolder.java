package me.hsgamer.topper.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TopHolder {
    public static final Logger LOGGER = Logger.getLogger(TopHolder.class.getName());
    private final Map<UUID, TopEntry> entryMap = new ConcurrentHashMap<>();
    private final AtomicReference<List<TopSnapshot>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final List<Consumer<TopEntry>> updateListeners = new ArrayList<>();
    private final List<Consumer<TopEntry>> removeListeners = new ArrayList<>();
    private final List<Consumer<TopEntry>> createListeners = new ArrayList<>();
    private final TopStorage topStorage;
    private final String name;

    protected TopHolder(TopStorage topStorage, String name) {
        this.topStorage = topStorage;
        this.name = name;
    }

    public abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

    public void onCreateEntry(TopEntry entry) {
        // EMPTY
    }

    public void onRemoveEntry(TopEntry entry) {
        // EMPTY
    }

    public void onUpdateEntry(TopEntry entry) {
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

    public final void notifyCreateEntry(TopEntry entry) {
        onCreateEntry(entry);
        createListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyRemoveEntry(TopEntry entry) {
        onRemoveEntry(entry);
        removeListeners.forEach(listener -> listener.accept(entry));
    }

    public final void notifyUpdateEntry(TopEntry entry) {
        onUpdateEntry(entry);
        updateListeners.forEach(listener -> listener.accept(entry));
    }

    public final void addCreateListener(Consumer<TopEntry> listener) {
        createListeners.add(listener);
    }

    public final void addRemoveListener(Consumer<TopEntry> listener) {
        removeListeners.add(listener);
    }

    public final void addUpdateListener(Consumer<TopEntry> listener) {
        updateListeners.add(listener);
    }

    public final void takeTopSnapshot() {
        List<TopSnapshot> list = entryMap.entrySet().stream()
                .map(entry -> new TopSnapshot(entry.getKey(), entry.getValue().getValue()))
                .sorted(Comparator.comparing(TopSnapshot::getValue).reversed())
                .collect(Collectors.toList());
        topSnapshot.set(list);

        Map<UUID, Integer> map = IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.toMap(i -> list.get(i).getUuid(), i -> i));
        indexMap.set(map);
    }

    public final CompletableFuture<Void> save(TopEntry entry, boolean onUnregister) {
        return topStorage.save(entry, onUnregister);
    }

    public final void register() {
        onRegister();
        topStorage.onRegister(this);
        topStorage.load(this)
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
        topStorage.onUnregister(this);
        onUnregister();
        entryMap.clear();
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
        createListeners.clear();
        removeListeners.clear();
        updateListeners.clear();
    }

    public final String getName() {
        return name;
    }

    public final TopStorage getTopStorage() {
        return topStorage;
    }

    public final List<TopSnapshot> getTop() {
        return topSnapshot.get();
    }

    public final int getTopIndex(UUID uuid) {
        return indexMap.get().getOrDefault(uuid, -1);
    }

    public final Optional<TopEntry> getEntry(UUID uuid) {
        return Optional.ofNullable(entryMap.get(uuid));
    }

    public final TopEntry getOrCreateEntry(UUID uuid) {
        return entryMap.computeIfAbsent(uuid, u -> {
            TopEntry entry = new TopEntry(uuid, this);
            notifyCreateEntry(entry);
            return entry;
        });
    }

    public final Optional<TopEntry> getEntryByIndex(int index) {
        List<TopSnapshot> list = getTop();
        if (index < 0 || index >= list.size()) return Optional.empty();
        UUID uuid = list.get(index).getUuid();
        return getEntry(uuid);
    }
}
