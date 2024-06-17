package me.hsgamer.topper.core.agent;

public interface Agent {
    default void start() {
        // EMPTY
    }

    default void stop() {
        // EMPTY
    }

    default void beforeStop() {
        // EMPTY
    }
}
