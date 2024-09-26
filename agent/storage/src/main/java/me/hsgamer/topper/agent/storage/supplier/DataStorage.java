package me.hsgamer.topper.agent.storage.supplier;

import me.hsgamer.topper.core.DataHolder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class DataStorage<K, V> {
    protected final DataHolder<K, V> holder;

    protected DataStorage(DataHolder<K, V> holder) {
        this.holder = holder;
    }

    public abstract CompletableFuture<Map<K, V>> load();

    public abstract CompletableFuture<Void> save(K key, V value, boolean urgent);

    public abstract CompletableFuture<Optional<V>> load(K key, boolean urgent);

    public DataHolder<K, V> getHolder() {
        return holder;
    }

    public void onRegister() {
        // EMPTY
    }

    public void onUnregister() {
        // EMPTY
    }
}
