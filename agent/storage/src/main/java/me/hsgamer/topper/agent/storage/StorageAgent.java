package me.hsgamer.topper.agent.storage;

import me.hsgamer.topper.agent.core.Agent;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.core.DataHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StorageAgent<K, V> implements Agent<K, V>, Runnable {
    private final Logger logger;
    private final DataHolder<K, V> holder;
    private final DataStorage<K, V> storage;
    private final Queue<K> queue = new ConcurrentLinkedQueue<>();
    private int maxEntryPerCall = 10;

    public StorageAgent(Logger logger, DataHolder<K, V> holder, DataStorage<K, V> storage) {
        this.logger = logger;
        this.holder = holder;
        this.storage = storage;
    }

    private void save(boolean urgent) {
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < (urgent || maxEntryPerCall <= 0 ? Integer.MAX_VALUE : maxEntryPerCall); i++) {
            K key = queue.poll();
            if (key == null) {
                break;
            }
            DataEntry<K, V> entry = holder.getOrCreateEntry(key);
            map.put(key, entry.getValue());
        }
        if (!map.isEmpty()) {
            storage.save(map, urgent).whenComplete((v, t) -> {
                if (t != null) {
                    logger.log(Level.SEVERE, "An error occurred while saving data", t);
                }
            });
        }
    }

    @Override
    public void start() {
        storage.onRegister();
        try {
            storage.load().forEach((uuid, value) -> holder.getOrCreateEntry(uuid).setValue(value, false));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load top entries for " + holder.getName(), e);
        }
    }

    @Override
    public void stop() {
        storage.onUnregister();
    }

    @Override
    public void beforeStop() {
        save(true);
    }

    @Override
    public void onUpdate(DataEntry<K, V> entry) {
        queue.add(entry.getKey());
    }

    @Override
    public void run() {
        save(false);
    }

    public DataStorage<K, V> getStorage() {
        return storage;
    }

    public void loadIfExists(K key, boolean urgent) {
        Optional<DataEntry<K, V>> optional = holder.getEntry(key);
        if (!optional.isPresent()) {
            return;
        }
        DataEntry<K, V> entry = optional.get();

        storage.load(key, urgent).whenComplete((result, t) -> {
            if (t != null) {
                logger.log(Level.SEVERE, "An error occurred while loading data", t);
            } else {
                result.ifPresent(entry::setValue);
            }
        });
    }

    public void setMaxEntryPerCall(int taskSaveEntryPerTick) {
        this.maxEntryPerCall = taskSaveEntryPerTick;
    }
}
