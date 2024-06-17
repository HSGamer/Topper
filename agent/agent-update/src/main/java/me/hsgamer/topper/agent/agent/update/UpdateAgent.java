package me.hsgamer.topper.agent.agent.update;

import me.hsgamer.topper.core.agent.Agent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class UpdateAgent<K, V> implements Agent, Runnable {
    public static final EntryTempFlag IS_UPDATING = new EntryTempFlag("isUpdating");
    public static final EntryTempFlag IGNORE_UPDATE = new EntryTempFlag("ignoreUpdate");
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
        holder.getListenerManager().add(DataHolder.EventStates.CREATE, entry -> updateQueue.add(entry.getKey()));
        holder.getListenerManager().add(DataHolder.EventStates.REMOVE, entry -> updateQueue.remove(entry.getKey()));
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
