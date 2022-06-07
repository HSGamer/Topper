package me.hsgamer.topper.core.agent.storage;

import me.hsgamer.topper.core.agent.TaskAgent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.flag.EntryTempFlag;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageAgent<T extends Comparable<T>, R> extends TaskAgent<R> {
    private static final Logger LOGGER = Logger.getLogger(StorageAgent.class.getName());
    private final Queue<UUID> saveQueue = new ConcurrentLinkedQueue<>();
    private final List<Runnable> onLoadListeners = new ArrayList<>();
    private final DataHolder<T> holder;
    private final DataStorage<T> storage;
    private int maxEntryPerCall = 10;

    public StorageAgent(DataStorage<T> storage) {
        this.holder = storage.getHolder();
        this.storage = storage;
    }

    private void save(DataEntry<T> entry, boolean onUnregister) {
        if (entry.hasFlag(EntryTempFlag.IS_SAVING)) return;
        if (!entry.hasFlag(EntryTempFlag.NEED_SAVING)) return;
        entry.removeFlag(EntryTempFlag.NEED_SAVING);
        entry.addFlag(EntryTempFlag.IS_SAVING);
        storage.save(entry.getUuid(), entry.getValue(), onUnregister).whenComplete((result, throwable) -> entry.removeFlag(EntryTempFlag.IS_SAVING));
    }

    @Override
    public void start() {
        holder.addRemoveListener(entry -> save(entry, true));
        holder.addUpdateListener(entry -> entry.addFlag(EntryTempFlag.NEED_SAVING));
        storage.onRegister();
        storage.load()
                .whenComplete((entries, throwable) -> {
                    if (throwable != null) {
                        LOGGER.log(Level.SEVERE, "Failed to load top entries", throwable);
                    }
                    if (entries != null) {
                        entries.forEach((uuid, value) -> holder.getOrCreateEntry(uuid).setValue(value));
                    }
                    onLoadListeners.forEach(Runnable::run);
                });
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
        storage.onUnregister();
    }

    @Override
    protected Runnable getRunnable() {
        return () -> {
            List<UUID> list = new ArrayList<>();
            for (int i = 0; i < maxEntryPerCall; i++) {
                UUID uuid = saveQueue.poll();
                if (uuid == null) {
                    break;
                }
                DataEntry<T> entry = holder.getOrCreateEntry(uuid);
                save(entry, false);
                list.add(uuid);
            }
            if (!list.isEmpty()) {
                saveQueue.addAll(list);
            }
        };
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }

    public void addOnLoadListener(Runnable runnable) {
        onLoadListeners.add(runnable);
    }
}
