package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.AutoSaveConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlStorage extends DataStorage<Double> {
    private static final TopperPlugin instance;
    private static final File baseFolder;

    static {
        instance = JavaPlugin.getPlugin(TopperPlugin.class);
        baseFolder = new File(instance.getDataFolder(), "top");
    }

    private final AutoSaveConfig config;

    public YamlStorage(DataHolder<Double> holder) {
        super(holder);
        config = new AutoSaveConfig(instance, new BukkitConfig(new File(baseFolder, holder.getName() + ".yml")));
    }

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
    public CompletableFuture<Void> save(UUID uuid, Double value, boolean onUnregister) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                config.set(uuid.toString(), value);
                future.complete(null);
            }
        };
        if (onUnregister) {
            runnable.run();
        } else {
            runnable.runTask(instance);
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
}
