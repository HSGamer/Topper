package me.hsgamer.topper.core.agent.snapshot;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SnapshotAgent<T extends Comparable<T>, R> extends TaskAgent<R> {
    private final AtomicReference<List<DataSnapshot<T>>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final DataHolder<T> holder;

    public SnapshotAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            List<DataSnapshot<T>> list = holder.getEntryMap().entrySet().stream()
                    .map(entry -> new DataSnapshot<>(entry.getKey(), entry.getValue().getValue()))
                    .sorted(Comparator.<DataSnapshot<T>, T>comparing(DataSnapshot::getValue).reversed())
                    .collect(Collectors.toList());
            topSnapshot.set(list);

            Map<UUID, Integer> map = IntStream.range(0, list.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> list.get(i).getUuid(), i -> i));
            indexMap.set(map);
        };
    }

    @Override
    public void stop() {
        super.stop();
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
    }

    public final List<DataSnapshot<T>> getTop() {
        return topSnapshot.get();
    }

    public final int getTopIndex(UUID uuid) {
        return indexMap.get().getOrDefault(uuid, -1);
    }

    public final Optional<DataEntry<T>> getEntryByIndex(int index) {
        List<DataSnapshot<T>> list = getTop();
        if (index < 0 || index >= list.size()) return Optional.empty();
        UUID uuid = list.get(index).getUuid();
        return holder.getEntry(uuid);
    }
}
