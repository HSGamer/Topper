package me.hsgamer.topper.agent.snapshot;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SnapshotAgent<K, V> implements Agent<K, V>, Runnable {
    private final AtomicReference<List<DataSnapshot<K, V>>> topSnapshot = new AtomicReference<>(Collections.emptyList());
    private final AtomicReference<Map<K, Integer>> indexMap = new AtomicReference<>(Collections.emptyMap());
    private final DataHolder<K, V> holder;
    private final List<Predicate<DataSnapshot<K, V>>> filters = new ArrayList<>();
    private Comparator<V> comparator;

    public SnapshotAgent(DataHolder<K, V> holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        List<DataSnapshot<K, V>> list = getUrgentSnapshot();
        topSnapshot.set(getUrgentSnapshot());

        Map<K, Integer> map = IntStream.range(0, list.size())
                .boxed()
                .collect(Collectors.toMap(i -> list.get(i).key, i -> i));
        indexMap.set(map);
    }

    @Override
    public void stop() {
        topSnapshot.set(Collections.emptyList());
        indexMap.set(Collections.emptyMap());
    }

    public List<DataSnapshot<K, V>> getUrgentSnapshot() {
        Stream<DataSnapshot<K, V>> stream = holder.getEntryMap().entrySet().stream()
                .map(entry -> new DataSnapshot<>(entry.getKey(), entry.getValue().getValue()))
                .filter(snapshot -> filters.stream().allMatch(filter -> filter.test(snapshot)));
        if (comparator != null) {
            stream = stream.sorted(Comparator.comparing(snapshot -> snapshot.value, comparator));
        }
        return stream.collect(Collectors.toList());
    }

    public List<DataSnapshot<K, V>> getSnapshot() {
        return topSnapshot.get();
    }

    public int getSnapshotIndex(K key) {
        return indexMap.get().getOrDefault(key, -1);
    }

    public Optional<DataSnapshot<K, V>> getSnapshotByIndex(int index) {
        List<DataSnapshot<K, V>> list = getSnapshot();
        if (index < 0 || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index));
    }

    public Optional<DataEntry<K, V>> getEntryByIndex(int index) {
        return getSnapshotByIndex(index).flatMap(snapshot -> holder.getEntry(snapshot.key));
    }

    public void setComparator(Comparator<V> comparator) {
        this.comparator = comparator;
    }

    public void addFilter(Predicate<DataSnapshot<K, V>> filter) {
        filters.add(filter);
    }
}
