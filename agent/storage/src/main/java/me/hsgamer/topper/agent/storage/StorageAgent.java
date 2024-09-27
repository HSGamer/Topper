package me.hsgamer.topper.agent.storage;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageAgent<K, V> implements Agent<K, V>, Runnable {
    public static final String NEED_SAVING = "needSaving";
    public static final String IS_SAVING = "isSaving";
    private final Queue<K> saveQueue = new ConcurrentLinkedQueue<>();
    private final Logger logger;
    private final DataHolder<K, V> holder;
    private final DataStorage<K, V> storage;
    private int maxEntryPerCall = 10;
    private boolean urgentSave = false;
    private boolean loadOnCreate = false;
    private boolean urgentLoad = true;

    public StorageAgent(Logger logger, DataStorage<K, V> storage) {
        this.logger = logger;
        this.holder = storage.getHolder();
        this.storage = storage;
    }

    private void save(DataEntry<K, V> entry) {
        if (entry.hasFlag(IS_SAVING)) return;
        if (!entry.hasFlag(NEED_SAVING)) return;
        entry.removeFlag(NEED_SAVING);
        entry.addFlag(IS_SAVING);
        storage.save(entry.getKey(), entry.getValue(), urgentSave).whenComplete((result, throwable) -> entry.removeFlag(IS_SAVING));
    }

    private void load(DataEntry<K, V> entry) {
        storage.load(entry.getKey(), urgentLoad).whenComplete((result, throwable) -> {
            if (throwable != null) {
                logger.log(Level.WARNING, throwable, () -> "Failed to load " + entry.getKey());
            } else {
                result.ifPresent(entry::setValue);
            }
        });
    }

    private void loadAll() {
        try {
            storage.load()
                    .forEach((uuid, value) -> holder.getOrCreateEntry(uuid).setValue(value, false));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load top entries for " + holder.getName(), e);
        }
    }

    public void loadIfExist(K key) {
        holder.getEntry(key).ifPresent(this::load);
    }

    @Override
    public void start() {
        storage.onRegister();
        loadAll();
    }

    @Override
    public void onCreate(DataEntry<K, V> entry) {
        saveQueue.add(entry.getKey());
        if (loadOnCreate) {
            load(entry);
        }
    }

    @Override
    public void onUpdate(DataEntry<K, V> entry) {
        entry.addFlag(NEED_SAVING);
    }

    @Override
    public void onRemove(DataEntry<K, V> entry) {
        save(entry);
        saveQueue.remove(entry.getKey());
    }

    @Override
    public void stop() {
        storage.onUnregister();
    }

    @Override
    public void beforeStop() {
        urgentSave = true;
    }

    @Override
    public void run() {
        List<K> list = new ArrayList<>();
        for (int i = 0; i < maxEntryPerCall; i++) {
            K k = saveQueue.poll();
            if (k == null) break;
            DataEntry<K, V> entry = holder.getOrCreateEntry(k);
            save(entry);
            list.add(k);
        }
        if (!list.isEmpty()) {
            saveQueue.addAll(list);
        }
    }

    public void setMaxEntryPerCall(int maxEntryPerCall) {
        this.maxEntryPerCall = maxEntryPerCall;
    }

    public void setUrgentLoad(boolean urgentLoad) {
        this.urgentLoad = urgentLoad;
    }

    public void setLoadOnCreate(boolean loadOnCreate) {
        this.loadOnCreate = loadOnCreate;
    }
}
