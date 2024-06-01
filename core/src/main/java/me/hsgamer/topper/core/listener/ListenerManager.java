package me.hsgamer.topper.core.listener;

import me.hsgamer.topper.core.entry.DataEntry;

import java.util.*;
import java.util.function.Consumer;

public class ListenerManager<K, V> {
    private final Map<EventState, List<Consumer<DataEntry<K, V>>>> entryListeners = new HashMap<>();
    private final Map<EventState, List<Runnable>> listeners = new HashMap<>();

    private List<Consumer<DataEntry<K, V>>> getEntryListeners(EventState state, boolean createIfAbsent) {
        if (createIfAbsent) {
            return entryListeners.computeIfAbsent(state, s -> new ArrayList<>());
        } else {
            return entryListeners.getOrDefault(state, Collections.emptyList());
        }
    }

    private List<Runnable> getListeners(EventState state, boolean createIfAbsent) {
        if (createIfAbsent) {
            return listeners.computeIfAbsent(state, s -> new ArrayList<>());
        } else {
            return listeners.getOrDefault(state, Collections.emptyList());
        }
    }

    public void add(EventState state, Consumer<DataEntry<K, V>> listener) {
        getEntryListeners(state, true).add(listener);
    }

    public void add(EventState state, Runnable listener) {
        getListeners(state, true).add(listener);
    }

    public void remove(EventState state, Consumer<DataEntry<K, V>> listener) {
        getEntryListeners(state, false).remove(listener);
    }

    public void remove(EventState state, Runnable listener) {
        getListeners(state, false).remove(listener);
    }

    public void clear() {
        entryListeners.clear();
        listeners.clear();
    }

    public void call(EventState state) {
        getListeners(state, false).forEach(Runnable::run);
    }

    public void call(EventState state, DataEntry<K, V> entry) {
        call(state);
        getEntryListeners(state, false).forEach(listener -> listener.accept(entry));
    }
}
