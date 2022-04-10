package me.hsgamer.topper.core;

public interface Initializer {
    default void onRegister() {
        // EMPTY
    }

    default void onUnregister() {
        // EMPTY
    }
}
