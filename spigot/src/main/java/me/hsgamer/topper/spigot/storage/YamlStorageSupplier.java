package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.config.AutoSaveConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class YamlStorageSupplier implements Function<DataHolder<Double>, DataStorage<Double>> {
    private static String baseFolderPath = "top";
    private final JavaPlugin plugin;
    private final File baseFolder;

    public YamlStorageSupplier(JavaPlugin plugin) {
        this.plugin = plugin;
        baseFolder = new File(plugin.getDataFolder(), baseFolderPath);
    }

    public static void setBaseFolderPath(String baseFolderPath) {
        YamlStorageSupplier.baseFolderPath = baseFolderPath;
    }

    @Override
    public DataStorage<Double> apply(DataHolder<Double> holder) {
        return new DataStorage<Double>(holder) {
            private final AutoSaveConfig config = new AutoSaveConfig(plugin, new BukkitConfig(new File(baseFolder, holder.getName() + ".yml")));

            @Override
            public CompletableFuture<Map<UUID, Double>> load() {
                Map<String, Object> values = config.getValues(false);
                return CompletableFuture.supplyAsync(() -> {
                    Map<UUID, Double> map = new HashMap<>();
                    values.forEach((uuid, value) -> map.put(UUID.fromString(uuid), Double.parseDouble(String.valueOf(value))));
                    return map;
                });
            }

            @Override
            public CompletableFuture<Void> save(UUID uuid, Double value, boolean urgent) {
                CompletableFuture<Void> future = new CompletableFuture<>();
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        config.set(uuid.toString(), value);
                        future.complete(null);
                    }
                };
                if (urgent) {
                    runnable.run();
                } else {
                    runnable.runTask(plugin);
                }
                return future;
            }

            @Override
            public CompletableFuture<Optional<Double>> load(UUID uuid, boolean urgent) {
                CompletableFuture<Optional<Double>> future = new CompletableFuture<>();
                BukkitRunnable runnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        Optional<Double> optional = Optional.ofNullable(config.get(uuid.toString()))
                                .map(Objects::toString)
                                .map(s -> {
                                    try {
                                        return Double.parseDouble(s);
                                    } catch (Exception e) {
                                        return null;
                                    }
                                });
                        future.complete(optional);
                    }
                };
                if (urgent) {
                    runnable.run();
                } else {
                    runnable.runTask(plugin);
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
