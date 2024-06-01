package me.hsgamer.topper.core.agent.snapshot;

public class DataSnapshot<K, V> {
    public final K key;
    public final V value;

    DataSnapshot(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
