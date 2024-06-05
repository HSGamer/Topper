package me.hsgamer.topper.core.listener;

public interface EventState {
    static EventState newState() {
        return new EventState() {
        };
    }
}
