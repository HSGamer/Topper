package me.hsgamer.topper.agent.update;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class UpdateAgent<K, V> implements Agent<K, V>, Runnable {
    private final Queue<K> updateQueue = new ConcurrentLinkedQueue<>();
    private final Map<K, Boolean> updatingMap = new ConcurrentHashMap<>();
    private final DataHolder<K, V> holder;
    private final Function<K, CompletableFuture<Optional<V>>> updateFunction;
    private int maxEntryPerCall = 10;

    public UpdateAgent(DataHolder<K, V> holder, Function<K, CompletableFuture<Optional<V>>> updateFunction) {
        this.holder = holder;
        this.updateFunction = updateFunction;
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }

    @Override
    public void run() {
        List<K> list = new ArrayList<>();
        for (int i = 0; i < maxEntryPerCall; i++) {
            K k = updateQueue.poll();
            if (k == null) {
                break;
            }
            DataEntry<K, V> entry = holder.getOrCreateEntry(k);
            updateEntry(entry);
            list.add(k);
        }
        if (!list.isEmpty()) {
            updateQueue.addAll(list);
        }
    }

    @Override
    public void onCreate(DataEntry<K, V> entry) {
        updateQueue.add(entry.getKey());
    }

    @Override
    public void onRemove(DataEntry<K, V> entry) {
        updateQueue.remove(entry.getKey());
    }

    private void updateEntry(DataEntry<K, V> entry) {
        K key = entry.getKey();
        if (updatingMap.getOrDefault(key, false)) {
            return;
        }
        updatingMap.put(key, true);
        updateFunction.apply(entry.getKey()).thenAccept(optional -> {
            optional.ifPresent(entry::setValue);
            updatingMap.put(key, false);
        });
    }
}
