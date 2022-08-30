package me.hsgamer.topper.core.agent;

public interface Agent {
    void start();

    void stop();

    default void beforeStop() {
        // EMPTY
    }
}
