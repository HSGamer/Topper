package me.hsgamer.topper.core.agent.update;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

public class UpdateAgent<T extends Comparable<T>, R> extends TaskAgent<R> {
    private final Queue<UUID> updateQueue = new ConcurrentLinkedQueue<>();
    private final List<Consumer<DataEntry<T>>> updateListeners = new ArrayList<>();
    private final DataHolder<T> holder;
    private int maxEntryPerCall = 10;
    private Function<UUID, CompletableFuture<Optional<T>>> updateFunction;

    public UpdateAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    public UpdateAgent<T, R> addUpdateListener(Consumer<DataEntry<T>> listener) {
        updateListeners.add(listener);
        return this;
    }

    public UpdateAgent<T, R> setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
        return this;
    }

    public UpdateAgent<T, R> setUpdateFunction(Function<UUID, CompletableFuture<Optional<T>>> updateFunction) {
        this.updateFunction = updateFunction;
        return this;
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            List<UUID> list = new ArrayList<>();
            for (int i = 0; i < maxEntryPerCall; i++) {
                UUID uuid = updateQueue.poll();
                if (uuid == null) {
                    break;
                }
                DataEntry<T> entry = holder.getOrCreateEntry(uuid);
                updateEntry(entry);
                list.add(uuid);
            }
            if (!list.isEmpty()) {
                updateQueue.addAll(list);
            }
        };
    }

    public void start() {
        if (updateFunction == null) {
            throw new IllegalStateException("Update function is not set");
        }
        super.start();
    }

    private void updateEntry(DataEntry<T> entry) {
        if (entry.hasFlag(EntryTempFlag.IS_UPDATING)) {
            return;
        }
        entry.addFlag(EntryTempFlag.IS_UPDATING);
        updateFunction.apply(entry.getUuid()).thenAccept(optional -> {
            if (optional.isPresent()) {
                entry.setValue(optional.get());
                notifyUpdateEntry(entry);
            }
            entry.removeFlag(EntryTempFlag.IS_UPDATING);
        });
    }

    public void notifyUpdateEntry(DataEntry<T> entry) {
        updateListeners.forEach(listener -> listener.accept(entry));
    }
}
