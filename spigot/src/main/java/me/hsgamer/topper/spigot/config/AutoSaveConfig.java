package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.DecorativeConfig;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoSaveConfig extends DecorativeConfig implements Runnable {
    private final Plugin plugin;
    private final AtomicBoolean needSaving = new AtomicBoolean(false);
    private final AtomicBoolean isSaving = new AtomicBoolean(false);
    private BukkitTask task;

    public AutoSaveConfig(Plugin plugin, Config config) {
        super(config);
        this.plugin = plugin;
    }

    @Override
    public void setup() {
        super.setup();
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 40, 40);
    }

    @Override
    public void set(String path, Object value) {
        super.set(path, value);
        needSaving.set(true);
    }

    public void finalSave() {
        task.cancel();
        if (isSaving.get()) return;
        save();
    }

    @Override
    public void run() {
        if (isSaving.get()) return;
        if (!needSaving.get()) return;
        needSaving.set(false);
        isSaving.set(true);
        Bukkit.getScheduler().runTask(plugin, () -> {
            save();
            isSaving.set(false);
        });
    }
}
