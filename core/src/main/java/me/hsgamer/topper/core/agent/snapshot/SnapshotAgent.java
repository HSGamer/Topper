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

public class SnapshotAgent<T, R> extends TaskAgent<R> {
    private final AtomicReference<List<DataSnapshot<T>>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<UUID, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final DataHolder<T> holder;
    private final List<Predicate<UUID>> filters = new ArrayList<>();
    private Comparator<T> comparator;

    public SnapshotAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            Stream<DataSnapshot<T>> stream = holder.getEntryMap().entrySet().stream()
                    .filter(entry -> filters.parallelStream().allMatch(filter -> filter.test(entry.getKey())))
                    .map(entry -> new DataSnapshot<>(entry.getKey(), entry.getValue().getValue()));
            if (comparator != null) {
                stream = stream.sorted(Comparator.<DataSnapshot<T>, T>comparing(snapshot -> snapshot.value, comparator).reversed());
            }
            List<DataSnapshot<T>> list = stream.collect(Collectors.toList());
            topSnapshot.set(list);

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

    public List<DataSnapshot<T>> getSnapshot() {
        return topSnapshot.get();
    }

    public int getSnapshotIndex(UUID uuid) {
        return indexMap.get().getOrDefault(uuid, -1);
    }

    public Optional<DataEntry<T>> getEntryByIndex(int index) {
        List<DataSnapshot<T>> list = getSnapshot();
        if (index < 0 || index >= list.size()) return Optional.empty();
        UUID uuid = list.get(index).uuid;
        return holder.getEntry(uuid);
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public void addFilter(Predicate<UUID> filter) {
        filters.add(filter);
    }
}
