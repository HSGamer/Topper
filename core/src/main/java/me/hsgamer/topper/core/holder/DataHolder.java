package me.hsgamer.topper.core.holder;

import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.core.listener.EntryListenerManager;
import me.hsgamer.topper.core.listener.ListenerManager;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolder<T> {
    private final Map<UUID, DataEntry<T>> entryMap = new ConcurrentHashMap<>();
    private final EntryListenerManager<T> removeListenerManager = new EntryListenerManager<>();
    private final EntryListenerManager<T> createListenerManager = new EntryListenerManager<>();
    private final EntryListenerManager<T> updateListenerManager = new EntryListenerManager<>();
    private final ListenerManager registerListenerManager = new ListenerManager();
    private final ListenerManager beforeUnregisterListenerManager = new ListenerManager();
    private final ListenerManager unregisterListenerManager = new ListenerManager();
    private final String name;

    protected DataHolder(String name) {
        this.name = name;
    }

    public T getDefaultValue() {
        return null;
    }

    public final EntryListenerManager<T> getRemoveListenerManager() {
        return removeListenerManager;
    }

    public final EntryListenerManager<T> getCreateListenerManager() {
        return createListenerManager;
    }

    public final EntryListenerManager<T> getUpdateListenerManager() {
        return updateListenerManager;
    }

    public final ListenerManager getRegisterListenerManager() {
        return registerListenerManager;
    }

    public final ListenerManager getBeforeUnregisterListenerManager() {
        return beforeUnregisterListenerManager;
    }

    public final ListenerManager getUnregisterListenerManager() {
        return unregisterListenerManager;
    }

    public final void register() {
        registerListenerManager.notifyListeners();
    }

    public final void unregister() {
        beforeUnregisterListenerManager.notifyListeners();

        entryMap.values().forEach(removeListenerManager::notifyListeners);
        entryMap.clear();

        unregisterListenerManager.notifyListeners();

        createListenerManager.clear();
        removeListenerManager.clear();
        updateListenerManager.clear();
        registerListenerManager.clear();
        beforeUnregisterListenerManager.clear();
        unregisterListenerManager.clear();
    }

    public final String getName() {
        return name;
    }

    public final Map<UUID, DataEntry<T>> getEntryMap() {
        return Collections.unmodifiableMap(entryMap);
    }

    public final Optional<DataEntry<T>> getEntry(UUID uuid) {
        return Optional.ofNullable(entryMap.get(uuid));
    }

    public final DataEntry<T> getOrCreateEntry(UUID uuid) {
        return entryMap.computeIfAbsent(uuid, u -> {
            DataEntry<T> entry = new DataEntry<>(u, this);
            createListenerManager.notifyListeners(entry);
            return entry;
        });
    }

    public final void removeEntry(UUID uuid) {
        Optional.ofNullable(entryMap.remove(uuid)).ifPresent(removeListenerManager::notifyListeners);
    }
}
