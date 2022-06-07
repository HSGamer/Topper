package me.hsgamer.topper.spigot.holder;

import me.hsgamer.topper.core.common.DataStorage;
import me.hsgamer.topper.core.top.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import org.bukkit.scheduler.BukkitTask;

public abstract class AutoUpdateNumberTopHolder extends TopHolder<Double> {
    protected final TopperPlugin instance;
    private BukkitTask updateTask;
    private BukkitTask saveTask;
    private BukkitTask snapshotTask;

    protected AutoUpdateNumberTopHolder(TopperPlugin instance, DataStorage<Double> topStorage, String name) {
        super(topStorage, name);
        this.instance = instance;
    }

    @Override
    public void onRegister() {
        setMaxEntryUpdatePerCall(MainConfig.TASK_UPDATE_ENTRY_PER_TICK.getValue());
        setMaxEntrySavePerCall(MainConfig.TASK_SAVE_ENTRY_PER_TICK.getValue());

        snapshotTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, getTopSnapshotRunnable(), 20L, 20L);

        int updateDelay = MainConfig.TASK_UPDATE_DELAY.getValue();
        updateTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, getUpdateRunnable(), updateDelay, updateDelay);

        int saveDelay = MainConfig.TASK_SAVE_DELAY.getValue();
        saveTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, getSaveRunnable(), saveDelay, saveDelay);
    }

    @Override
    public void onUnregister() {
        updateTask.cancel();
        snapshotTask.cancel();
        saveTask.cancel();
        super.onUnregister();
    }
}
