package me.hsgamer.topper.core.common;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public abstract class UpdatableDataHolder<T extends Comparable<T>> extends DataHolder<T> {
    public static final EntryTempFlag IS_UPDATING = new EntryTempFlag("isUpdating");
    private final Queue<UUID> updateQueue = new ConcurrentLinkedQueue<>();
    private final Queue<UUID> saveQueue = new ConcurrentLinkedQueue<>();
    private final List<Consumer<DataEntry<T>>> updateListeners = new ArrayList<>();
    private int maxEntryUpdatePerCall = 10;
    private int maxEntrySavePerCall = 10;

    protected UpdatableDataHolder(DataStorage<T> dataStorage, String name) {
        super(dataStorage, name);
    }

    public abstract CompletableFuture<Optional<T>> updateNewValue(UUID uuid);

    public void onUpdateEntry(DataEntry<T> entry) {
        // EMPTY
    }

    public final void notifyUpdateEntry(DataEntry<T> entry) {
        onUpdateEntry(entry);
        updateListeners.forEach(listener -> listener.accept(entry));
    }

    public final void addUpdateListener(Consumer<DataEntry<T>> listener) {
        updateListeners.add(listener);
    }

    public final void updateEntry(DataEntry<T> entry) {
        if (entry.hasFlag(IS_UPDATING)) {
            return;
        }
        entry.addFlag(IS_UPDATING);
        updateNewValue(entry.getUuid()).thenAccept(optional -> {
            if (optional.isPresent()) {
                entry.setValue(optional.get());
                notifyUpdateEntry(entry);
            }
            entry.removeFlag(IS_UPDATING);
        });
    }

    public final Runnable getUpdateRunnable() {
        return () -> {
            List<UUID> list = new ArrayList<>();
            for (int i = 0; i < maxEntryUpdatePerCall; i++) {
                UUID uuid = updateQueue.poll();
                if (uuid == null) {
                    break;
                }
                DataEntry<T> entry = getOrCreateEntry(uuid);
                updateEntry(entry);
                list.add(uuid);
            }
            if (!list.isEmpty()) {
                updateQueue.addAll(list);
            }
        };
    }

    public final Runnable getSaveRunnable() {
        return () -> {
            List<UUID> list = new ArrayList<>();
            for (int i = 0; i < maxEntrySavePerCall; i++) {
                UUID uuid = saveQueue.poll();
                if (uuid == null) {
                    break;
                }
                DataEntry<T> entry = getOrCreateEntry(uuid);
                entry.save();
                list.add(uuid);
            }
            if (!list.isEmpty()) {
                saveQueue.addAll(list);
            }
        };
    }

    public final void setMaxEntryUpdatePerCall(int maxEntryUpdatePerCall) {
        this.maxEntryUpdatePerCall = maxEntryUpdatePerCall;
    }

    public final void setMaxEntrySavePerCall(int maxEntrySavePerCall) {
        this.maxEntrySavePerCall = maxEntrySavePerCall;
    }

    @Override
    public void onCreateEntry(DataEntry<T> entry) {
        updateQueue.add(entry.getUuid());
        saveQueue.add(entry.getUuid());
    }

    @Override
    public void onRemoveEntry(DataEntry<T> entry) {
        updateQueue.remove(entry.getUuid());
        saveQueue.remove(entry.getUuid());
    }
}
