package me.hsgamer.topper.spigot.agent.storage.simple.supplier;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.topper.agent.storage.DataStorage;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.supplier.DataStorageSupplier;
import me.hsgamer.topper.core.DataHolder;
import me.hsgamer.topper.spigot.agent.storage.simple.util.AutoSaveConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ConfigStorageSupplier<K, V> implements DataStorageSupplier<K, V> {
    private final UnaryOperator<Runnable> runTaskFunction;
    private final Executor mainThreadExecutor;
    private final UnaryOperator<String> configNameProvider;
    private final Function<File, Config> configProvider;
    private final File holderBaseFolder;
    private final FlatEntryConverter<K, V> converter;

    public ConfigStorageSupplier(
            UnaryOperator<Runnable> runTaskFunction,
            Executor mainThreadExecutor,
            UnaryOperator<String> configNameProvider,
            Function<File, Config> configProvider,
            File holderBaseFolder,
            FlatEntryConverter<K, V> converter
    ) {
        this.runTaskFunction = runTaskFunction;
        this.mainThreadExecutor = mainThreadExecutor;
        this.configNameProvider = configNameProvider;
        this.configProvider = configProvider;
        this.holderBaseFolder = holderBaseFolder;
        this.converter = converter;
    }

    @Override
    public DataStorage<K, V> getStorage(DataHolder<K, V> holder) {
        return new DataStorage<K, V>() {
            private final AutoSaveConfig config = new AutoSaveConfig(configProvider.apply(new File(holderBaseFolder, configNameProvider.apply(holder.getName()))), runTaskFunction);

            @Override
            public Map<K, V> load() {
                Map<String[], Object> values = config.getValues(false);
                Map<K, V> map = new HashMap<>();
                values.forEach((path, value) -> {
                    V finalValue = converter.toValue(value);
                    if (finalValue != null) {
                        K finalKey = converter.toKey(path[0]);
                        map.put(finalKey, finalValue);
                    }
                });
                return map;
            }

            @Override
            public CompletableFuture<Void> save(Map<K, V> map, boolean urgent) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    map.forEach((key, value) -> config.set(converter.toRawValue(value), converter.toRawKey(key)));
                    future.complete(null);
                };
                if (urgent) {
                    runnable.run();
                } else {
                    mainThreadExecutor.execute(runnable);
                }
                return future;
            }

            @Override
            public CompletableFuture<Optional<V>> load(K key, boolean urgent) {
                CompletableFuture<Optional<V>> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    Optional<V> optional = Optional.ofNullable(config.get(converter.toRawKey(key))).map(converter::toValue);
                    future.complete(optional);
                };
                if (urgent) {
                    runnable.run();
                } else {
                    mainThreadExecutor.execute(runnable);
                }
                return future;
            }

            @Override
            public void onRegister() {
                config.setup();
            }

            @Override
            public void onUnregister() {
                config.finalSave();
            }
        };
    }
}
