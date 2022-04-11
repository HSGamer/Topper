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

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AutoUpdateTopHolder extends TopHolder {
    protected final TopperPlugin instance;
    private final Queue<UUID> updateQueue = new ConcurrentLinkedQueue<>();
    private final Queue<UUID> saveQueue = new ConcurrentLinkedQueue<>();
    private BukkitTask updateTask;
    private BukkitTask saveTask;
    private BukkitTask snapshotTask;

    protected AutoUpdateTopHolder(TopperPlugin instance, TopStorage topStorage, String name) {
        super(topStorage, name);
        this.instance = instance;
    }

    @Override
    public void onRegister() {
        snapshotTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, this::takeTopSnapshot, 20L, 20L);
        updateTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {
            UUID uuid = updateQueue.poll();
            if (uuid != null) {
                TopEntry entry = getOrCreateEntry(uuid);
                entry.update();
                updateQueue.add(uuid);
            }
        }, 10, 10);
        saveTask = instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, () -> {
            UUID uuid = saveQueue.poll();
            if (uuid != null) {
                TopEntry entry = getOrCreateEntry(uuid);
                entry.save();
                saveQueue.add(uuid);
            }
        }, 10, 10);
    }

    @Override
    public void onUnregister() {
        updateTask.cancel();
        snapshotTask.cancel();
        saveTask.cancel();
    }

    @Override
    public void onUpdateEntry(TopEntry entry) {
        Bukkit.getPluginManager().callEvent(new TopEntryUpdateEvent(entry));
    }

    @Override
    public void onCreateEntry(TopEntry entry) {
        updateQueue.add(entry.getUuid());
        saveQueue.add(entry.getUuid());
        Bukkit.getPluginManager().callEvent(new TopEntryCreateEvent(entry));
    }

    @Override
    public void onRemoveEntry(TopEntry entry) {
        updateQueue.remove(entry.getUuid());
        saveQueue.remove(entry.getUuid());
        Bukkit.getPluginManager().callEvent(new TopEntryRemoveEvent(entry));
    }
}
