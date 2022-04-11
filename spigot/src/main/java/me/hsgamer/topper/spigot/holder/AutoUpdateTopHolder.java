package me.hsgamer.topper.spigot.holder;

import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.event.TopEntryCreateEvent;
import me.hsgamer.topper.spigot.event.TopEntryRemoveEvent;
import me.hsgamer.topper.spigot.event.TopEntryUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class AutoUpdateTopHolder extends TopHolder {
    protected final TopperPlugin instance;
    private final Map<UUID, BukkitTask> updateTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> saveTasks = new HashMap<>();
    private BukkitTask snapshotTask;

    protected AutoUpdateTopHolder(TopperPlugin instance, TopStorage topStorage, String name) {
        super(topStorage, name);
        this.instance = instance;
    }

    @Override
    public void onRegister() {
        snapshotTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, this::takeTopSnapshot, 20L, 20L);
    }

    @Override
    public void onUnregister() {
        snapshotTask.cancel();
    }

    @Override
    public void onUpdateEntry(TopEntry entry) {
        Bukkit.getPluginManager().callEvent(new TopEntryUpdateEvent(entry));
    }

    @Override
    public void onCreateEntry(TopEntry entry) {
        updateTasks.put(entry.getUuid(), instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, entry::update, 20L, 20L));
        saveTasks.put(entry.getUuid(), instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, entry::save, 30L, 30L));
        Bukkit.getPluginManager().callEvent(new TopEntryCreateEvent(entry));
    }

    @Override
    public void onRemoveEntry(TopEntry entry) {
        Bukkit.getPluginManager().callEvent(new TopEntryRemoveEvent(entry));
        updateTasks.remove(entry.getUuid()).cancel();
        saveTasks.remove(entry.getUuid()).cancel();
    }
}
