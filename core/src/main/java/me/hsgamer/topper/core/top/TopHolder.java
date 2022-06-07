package me.hsgamer.topper.core.top;

import me.hsgamer.topper.core.common.DataEntry;
import me.hsgamer.topper.core.common.DataStorage;
import me.hsgamer.topper.core.common.UpdatableDataHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TopHolder<T extends Comparable<T>> extends UpdatableDataHolder<T> {
    private final AtomicReference<List<TopSnapshot<T>>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());

    protected TopHolder(DataStorage<T> dataStorage, String name) {
        super(dataStorage, name);
    }

    public final Runnable getTopSnapshotRunnable() {
        return () -> {
            List<TopSnapshot<T>> list = getEntryMap().entrySet().stream()
                    .map(entry -> new TopSnapshot<>(entry.getKey(), entry.getValue().getValue()))
                    .sorted(Comparator.<TopSnapshot<T>, T>comparing(TopSnapshot::getValue).reversed())
                    .collect(Collectors.toList());
            topSnapshot.set(list);

            Map<UUID, Integer> map = IntStream.range(0, list.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> list.get(i).getUuid(), i -> i));
            indexMap.set(map);
        };
    }

    @Override
    public void onUnregister() {
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
    }

    public final List<TopSnapshot<T>> getTop() {
        return topSnapshot.get();
    }

    public final int getTopIndex(UUID uuid) {
        return indexMap.get().getOrDefault(uuid, -1);
    }

    public final Optional<DataEntry<T>> getEntryByIndex(int index) {
        List<TopSnapshot<T>> list = getTop();
        if (index < 0 || index >= list.size()) return Optional.empty();
        UUID uuid = list.get(index).getUuid();
        return getEntry(uuid);
    }
}
