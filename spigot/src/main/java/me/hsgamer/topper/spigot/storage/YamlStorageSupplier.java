package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.hscore.config.PathString;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.extra.storage.converter.FlatEntryConverter;
import me.hsgamer.topper.spigot.config.AutoSaveConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class YamlStorageSupplier<T> implements Function<DataHolder<T>, DataStorage<T>> {
    private final JavaPlugin plugin;
    private final File baseFolder;
    private final FlatEntryConverter<T> converter;

    public YamlStorageSupplier(JavaPlugin plugin, File baseFolder, FlatEntryConverter<T> converter) {
        this.plugin = plugin;
        this.baseFolder = baseFolder;
        this.converter = converter;
    }

    @Override
    public DataStorage<T> apply(DataHolder<T> holder) {
        return new DataStorage<T>(holder) {
            private final AutoSaveConfig config = new AutoSaveConfig(plugin, new BukkitConfig(new File(baseFolder, holder.getName() + ".yml")));

            @Override
            public CompletableFuture<Map<UUID, T>> load() {
                Map<PathString, Object> values = config.getValues(false);
                return CompletableFuture.supplyAsync(() -> {
                    Map<UUID, T> map = new HashMap<>();
                    values.forEach((uuidPath, value) -> {
                        T finalValue = converter.toValue(value);
                        if (finalValue != null) {
                            map.put(UUID.fromString(uuidPath.getLastPath()), finalValue);
                        }
                    });
                    return map;
                });
            }

            @Override
            public CompletableFuture<Void> save(UUID uuid, T value, boolean urgent) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    config.set(new PathString(uuid.toString()), converter.toRaw(value));
                    future.complete(null);
                };
                if (urgent) {
                    runnable.run();
                } else {
                    Scheduler.plugin(plugin).sync().runTask(runnable);
                }
                return future;
            }

            @Override
            public CompletableFuture<Optional<T>> load(UUID uuid, boolean urgent) {
                CompletableFuture<Optional<T>> future = new CompletableFuture<>();
                Runnable runnable = () -> {
                    Optional<T> optional = Optional.ofNullable(config.get(new PathString(uuid.toString()))).map(converter::toValue);
                    future.complete(optional);
                };
                if (urgent) {
                    runnable.run();
                } else {
                    Scheduler.plugin(plugin).sync().runTask(runnable);
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
