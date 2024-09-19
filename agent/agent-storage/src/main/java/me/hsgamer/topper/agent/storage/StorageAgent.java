package me.hsgamer.topper.agent.storage;

import me.hsgamer.topper.agent.storage.supplier.DataStorage;
import me.hsgamer.topper.core.agent.Agent;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.listener.EventState;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageAgent<K, V> implements Agent, Runnable {
    public static final EventState LOAD_EVENT = EventState.newState();
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
        super();
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

    public void loadIfExist(K key) {
        holder.getEntry(key).ifPresent(this::load);
    }

    @Override
    public void start() {
        holder.getListenerManager().add(DataHolder.EventStates.CREATE, entry -> {
            saveQueue.add(entry.getKey());
            if (loadOnCreate) {
                load(entry);
            }
        });
        holder.getListenerManager().add(DataHolder.EventStates.REMOVE, entry -> {
            save(entry);
            saveQueue.remove(entry.getKey());
        });
        holder.getListenerManager().add(DataHolder.EventStates.UPDATE, entry -> entry.addFlag(NEED_SAVING));
        storage.onRegister();
        storage.load()
                .whenComplete((entries, throwable) -> {
                    if (throwable != null) {
                        logger.log(Level.SEVERE, "Failed to load top entries", throwable);
                    }
                    if (entries != null) {
                        entries.forEach((uuid, value) -> holder.getOrCreateEntry(uuid).setValue(value, false));
                    }
                    holder.getListenerManager().call(LOAD_EVENT);
                });
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
