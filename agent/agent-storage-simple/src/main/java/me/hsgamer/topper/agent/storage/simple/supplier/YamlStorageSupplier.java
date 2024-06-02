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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class YamlStorageSupplier<T> implements DataStorageSupplier<UUID, T> {
    private final UnaryOperator<Runnable> runTaskFunction;
    private final Executor mainThreadExecutor;
    private final Function<File, Config> yamlConfigProvider;
    private final File baseFolder;
    private final FlatEntryConverter<UUID, T> converter;

    public YamlStorageSupplier(
            UnaryOperator<Runnable> runTaskFunction,
            Executor mainThreadExecutor,
            Function<File, Config> yamlConfigProvider,
            File baseFolder,
            FlatEntryConverter<UUID, T> converter
    ) {
        this.runTaskFunction = runTaskFunction;
        this.mainThreadExecutor = mainThreadExecutor;
        this.yamlConfigProvider = yamlConfigProvider;
        this.baseFolder = baseFolder;
        this.converter = converter;
    }

    @Override
    public DataStorage<UUID, T> getStorage(DataHolder<UUID, T> holder) {
        return new DataStorage<UUID, T>(holder) {
            private final AutoSaveConfig config = new AutoSaveConfig(yamlConfigProvider.apply(new File(baseFolder, holder.getName() + ".yml")), runTaskFunction);

            @Override
            public CompletableFuture<Map<UUID, T>> load() {
                Map<PathString, Object> values = config.getValues(false);
                return CompletableFuture.supplyAsync(() -> {
                    Map<UUID, T> map = new HashMap<>();
                    values.forEach((path, value) -> {
                        T finalValue = converter.toValue(value);
                        if (finalValue != null) {
                            map.put(converter.toKey(path.getLastPath()), finalValue);
                        }
                    });
                    return map;
                });
            }

            @Override
            public CompletableFuture<Void> save(UUID uuid, T value, boolean urgent) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    config.set(new PathString(converter.toRawKey(uuid)), converter.toRawValue(value));
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
            public CompletableFuture<Optional<T>> load(UUID uuid, boolean urgent) {
                CompletableFuture<Optional<T>> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    Optional<T> optional = Optional.ofNullable(config.get(new PathString(converter.toRawKey(uuid)))).map(converter::toValue);
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
