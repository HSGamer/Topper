package me.hsgamer.topper.core.listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerManager {
    private final List<Runnable> listeners = new ArrayList<>();

    public void add(Runnable listener) {
        listeners.add(listener);
    }

    public void remove(Runnable listener) {
        listeners.remove(listener);
    }

    public void clear() {
        listeners.clear();
    }

    public void notifyListeners() {
        listeners.forEach(Runnable::run);
    }
}
