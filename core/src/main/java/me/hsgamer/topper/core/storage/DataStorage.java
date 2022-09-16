package me.hsgamer.topper.core.storage;

import me.hsgamer.topper.core.holder.DataHolder;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public abstract class DataStorage<T> {
    protected static final Logger LOGGER = Logger.getLogger(DataStorage.class.getName());
    protected final DataHolder<T> holder;

    protected DataStorage(DataHolder<T> holder) {
        this.holder = holder;
    }

    public abstract CompletableFuture<Map<UUID, T>> load();

    public abstract CompletableFuture<Void> save(UUID uuid, T value, boolean urgent);

    public abstract CompletableFuture<Optional<T>> load(UUID uuid, boolean urgent);

    public DataHolder<T> getHolder() {
        return holder;
    }

    public void onRegister() {
        // EMPTY
    }

    public void onUnregister() {
        // EMPTY
    }
}
