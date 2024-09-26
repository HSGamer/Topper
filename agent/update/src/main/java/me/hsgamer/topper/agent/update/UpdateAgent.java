package me.hsgamer.topper.agent.update;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class UpdateAgent<K, V> implements Agent<K, V>, Runnable {
    public static final String IS_UPDATING = "isUpdating";
    public static final String IGNORE_UPDATE = "ignoreUpdate";
    private final Queue<K> updateQueue = new ConcurrentLinkedQueue<>();
    private final DataHolder<K, V> holder;
    private int maxEntryPerCall = 10;
    private Function<K, CompletableFuture<Optional<V>>> updateFunction;

    public UpdateAgent(DataHolder<K, V> holder) {
        super();
        this.holder = holder;
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }

    public void setUpdateFunction(Function<K, CompletableFuture<Optional<V>>> updateFunction) {
        this.updateFunction = updateFunction;
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
    public void start() {
        if (updateFunction == null) {
            throw new IllegalStateException("Update function is not set");
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
        if (entry.hasFlag(IGNORE_UPDATE)) return;
        if (entry.hasFlag(IS_UPDATING)) return;
        entry.addFlag(IS_UPDATING);
        updateFunction.apply(entry.getKey()).thenAccept(optional -> {
            optional.ifPresent(entry::setValue);
            entry.removeFlag(IS_UPDATING);
        });
    }
}
