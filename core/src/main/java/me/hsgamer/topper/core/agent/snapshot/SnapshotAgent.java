package me.hsgamer.topper.core.agent.snapshot;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SnapshotAgent<T> extends TaskAgent {
    private final AtomicReference<List<DataSnapshot<T>>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final DataHolder<T> holder;
    private final List<Predicate<DataSnapshot<T>>> filters = new ArrayList<>();
    private Comparator<T> comparator;

    public SnapshotAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            List<DataSnapshot<T>> list = getUrgentSnapshot();
            topSnapshot.set(getUrgentSnapshot());

            Map<UUID, Integer> map = IntStream.range(0, list.size())
                    .boxed()
                    .collect(Collectors.toMap(i -> list.get(i).uuid, i -> i));
            indexMap.set(map);
        };
    }

    @Override
    public void stop() {
        super.stop();
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
    }

    public List<DataSnapshot<T>> getUrgentSnapshot() {
        Stream<DataSnapshot<T>> stream = holder.getEntryMap().entrySet().stream()
                .map(entry -> new DataSnapshot<>(entry.getKey(), entry.getValue().getValue()))
                .filter(snapshot -> filters.stream().allMatch(filter -> filter.test(snapshot)));
        if (comparator != null) {
            stream = stream.sorted(Comparator.<DataSnapshot<T>, T>comparing(snapshot -> snapshot.value, comparator).reversed());
        }
        return stream.collect(Collectors.toList());
    }

    public List<DataSnapshot<T>> getSnapshot() {
        return topSnapshot.get();
    }

    public int getSnapshotIndex(UUID uuid) {
        return indexMap.get().getOrDefault(uuid, -1);
    }

    public Optional<DataSnapshot<T>> getSnapshotByIndex(int index) {
        List<DataSnapshot<T>> list = getSnapshot();
        if (index < 0 || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index));
    }

    public Optional<DataEntry<T>> getEntryByIndex(int index) {
        return getSnapshotByIndex(index).flatMap(snapshot -> holder.getEntry(snapshot.uuid));
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void addFilter(Predicate<DataSnapshot<T>> filter) {
        filters.add(filter);
    }
}
