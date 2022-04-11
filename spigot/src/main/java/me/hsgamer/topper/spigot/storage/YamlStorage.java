package me.hsgamer.topper.spigot.storage;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.AutoSaveConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class YamlStorage implements TopStorage {
    private static final TopperPlugin instance;
    private static final File baseFolder;
    private static final Map<String, AutoSaveConfig> configs = new HashMap<>();

    static {
        instance = JavaPlugin.getPlugin(TopperPlugin.class);
        baseFolder = new File(instance.getDataFolder(), "top");
    }

    private AutoSaveConfig getConfig(String name) {
        return configs.computeIfAbsent(name, s -> {
            AutoSaveConfig config = new AutoSaveConfig(instance, new BukkitConfig(new File(baseFolder, s + ".yml")));
            config.setup();
            return config;
        });
    }

    private void removeConfig(String name) {
        AutoSaveConfig config = configs.remove(name);
        if (config != null) {
            config.finalSave();
        }
    }

    @Override
    public CompletableFuture<Map<UUID, BigDecimal>> load(TopHolder holder) {
        Config config = getConfig(holder.getName());
        Map<String, Object> values = config.getValues(false);
        return CompletableFuture.supplyAsync(() -> {
            Map<UUID, BigDecimal> map = new HashMap<>();
            values.forEach((uuid, value) -> map.put(UUID.fromString(uuid), new BigDecimal(String.valueOf(value))));
            return map;
        });
    }

    @Override
    public void save(TopEntry topEntry, boolean onUnregister) {
        Config config = getConfig(topEntry.getTopHolder().getName());
        config.set(topEntry.getUuid().toString(), topEntry.getValue().toString());
    }

    @Override
    public void onUnregister(TopHolder holder) {
        removeConfig(holder.getName());
    }
}
