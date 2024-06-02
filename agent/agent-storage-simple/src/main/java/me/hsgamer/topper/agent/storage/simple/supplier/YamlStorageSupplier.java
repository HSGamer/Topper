package me.hsgamer.topper.agent.storage.simple.supplier;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.topper.agent.storage.simple.converter.FlatEntryConverter;
import me.hsgamer.topper.agent.storage.simple.util.AutoSaveConfig;
import me.hsgamer.topper.agent.storage.supplier.DataStorage;
import me.hsgamer.topper.agent.storage.supplier.DataStorageSupplier;
import me.hsgamer.topper.core.holder.DataHolder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class YamlStorageSupplier<K, V> implements DataStorageSupplier<K, V> {
    private final UnaryOperator<Runnable> runTaskFunction;
    private final Executor mainThreadExecutor;
    private final Function<File, Config> yamlConfigProvider;
    private final File baseFolder;
    private final FlatEntryConverter<K, V> converter;

    public YamlStorageSupplier(
            UnaryOperator<Runnable> runTaskFunction,
            Executor mainThreadExecutor,
            Function<File, Config> yamlConfigProvider,
            File baseFolder,
            FlatEntryConverter<K, V> converter
    ) {
        this.runTaskFunction = runTaskFunction;
        this.mainThreadExecutor = mainThreadExecutor;
        this.yamlConfigProvider = yamlConfigProvider;
        this.baseFolder = baseFolder;
        this.converter = converter;
    }

    @Override
    public DataStorage<K, V> getStorage(DataHolder<K, V> holder) {
        return new DataStorage<K, V>(holder) {
            private final AutoSaveConfig config = new AutoSaveConfig(yamlConfigProvider.apply(new File(baseFolder, holder.getName() + ".yml")), runTaskFunction);

            @Override
            public CompletableFuture<Map<K, V>> load() {
                Map<PathString, Object> values = config.getValues(false);
                return CompletableFuture.supplyAsync(() -> {
                    Map<K, V> map = new HashMap<>();
                    values.forEach((path, value) -> {
                        V finalValue = converter.toValue(value);
                        if (finalValue != null) {
                            K finalKey = converter.toKey(path.getLastPath());
                            map.put(finalKey, finalValue);
                        }
                    });
                    return map;
                });
            }

            @Override
            public CompletableFuture<Void> save(K key, V value, boolean urgent) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    config.set(new PathString(converter.toRawKey(key)), converter.toRawValue(value));
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
                    Optional<V> optional = Optional.ofNullable(config.get(new PathString(converter.toRawKey(key)))).map(converter::toValue);
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
