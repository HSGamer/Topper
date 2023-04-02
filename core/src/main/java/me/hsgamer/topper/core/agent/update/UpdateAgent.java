package me.hsgamer.topper.core.agent.update;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

public class UpdateAgent<T> extends TaskAgent {
    public static final EntryTempFlag IS_UPDATING = new EntryTempFlag("isUpdating");
    public static final EntryTempFlag IGNORE_UPDATE = new EntryTempFlag("ignoreUpdate");
    private final Queue<UUID> updateQueue = new ConcurrentLinkedQueue<>();
    private final DataHolder<T> holder;
    private int maxEntryPerCall = 10;
    private Function<UUID, CompletableFuture<Optional<T>>> updateFunction;

    public UpdateAgent(DataHolder<T> holder) {
        this.holder = holder;
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }

    public void setUpdateFunction(Function<UUID, CompletableFuture<Optional<T>>> updateFunction) {
        this.updateFunction = updateFunction;
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

    @Override
    public void start() {
        if (updateFunction == null) {
            throw new IllegalStateException("Update function is not set");
        }
        holder.addCreateListener(entry -> updateQueue.add(entry.getUuid()));
        holder.addRemoveListener(entry -> updateQueue.remove(entry.getUuid()));
        super.start();
    }

    private void updateEntry(DataEntry<T> entry) {
        if (entry.hasFlag(IGNORE_UPDATE)) return;
        if (entry.hasFlag(IS_UPDATING)) return;
        entry.addFlag(IS_UPDATING);
        updateFunction.apply(entry.getUuid()).thenAccept(optional -> {
            optional.ifPresent(entry::setValue);
            entry.removeFlag(IS_UPDATING);
        });
    }
}
