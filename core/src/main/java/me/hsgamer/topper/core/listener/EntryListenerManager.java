package me.hsgamer.topper.core.listener;

import me.hsgamer.topper.core.entry.DataEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EntryListenerManager<T> {
    private final List<Consumer<DataEntry<T>>> listeners = new ArrayList<>();

    public void add(Consumer<DataEntry<T>> listener) {
        listeners.add(listener);
    }

    public void remove(Consumer<DataEntry<T>> listener) {
        listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public void notifyListeners(DataEntry<T> entry) {
        listeners.forEach(listener -> listener.accept(entry));
    }
}
