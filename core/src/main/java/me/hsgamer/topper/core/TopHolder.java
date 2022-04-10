package me.hsgamer.topper.core;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TopHolder implements Initializer {
    private final Map<UUID, TopEntry> entryMap = new ConcurrentHashMap<>();
    private final AtomicReference<List<TopSnapshot>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final TopStorage topStorage;

    protected TopHolder(TopStorage topStorage) {
        this.topStorage = topStorage;
    }

    public abstract CompletableFuture<Optional<BigDecimal>> updateNewValue(UUID uuid);

    public void onCreateEntry(TopEntry entry) {
        // EMPTY
    }

    public void onRemoveEntry(TopEntry entry) {
        // EMPTY
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

    public final void save(TopEntry entry) {
        topStorage.save(entry, false);
    }

    public final void register() {
        onRegister();
        topStorage.onRegister();
        topStorage.load().thenAccept(valueEntryMap -> valueEntryMap.forEach((uuid, value) -> getOrCreateEntry(uuid).setValue(value)));
    }

    public final void unregister() {
        entryMap.values().forEach(entry -> {
            onRemoveEntry(entry);
            topStorage.save(entry, true);
        });
        topStorage.onUnregister();
        onUnregister();
        entryMap.clear();
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
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
            onCreateEntry(entry);
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
