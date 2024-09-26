package me.hsgamer.topper.agent.core;

import me.hsgamer.topper.core.DataEntry;

public interface Agent<K, V> {
    default void start() {
        // EMPTY
    }

    default void stop() {
        // EMPTY
    }

    default void beforeStop() {
        // EMPTY
    }

    default void onCreate(DataEntry<K, V> entry) {
        // EMPTY
    }

    default void onUpdate(DataEntry<K, V> entry) {
        // EMPTY
    }

    default void onRemove(DataEntry<K, V> entry) {
        // EMPTY
    }

    default void onUnregister(DataEntry<K, V> entry) {
        // EMPTY
    }
}
