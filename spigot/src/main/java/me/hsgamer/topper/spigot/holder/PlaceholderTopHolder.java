package me.hsgamer.topper.spigot.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.event.TopEntryCreateEvent;
import me.hsgamer.topper.spigot.event.TopEntryRemoveEvent;
import me.hsgamer.topper.spigot.event.TopEntryUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderTopHolder extends TopHolder {
    private final TopperPlugin instance;
    private final String placeholder;
    private final Map<UUID, BukkitTask> updateTasks = new HashMap<>();
    private final Map<UUID, BukkitTask> saveTasks = new HashMap<>();
    private BukkitTask snapshotTask;

    public PlaceholderTopHolder(TopperPlugin instance, TopStorage topStorage, String name, String placeholder) {
        super(topStorage, name);
        this.instance = instance;
        this.placeholder = placeholder;
    }

    @Override
    public CompletableFuture<Optional<BigDecimal>> updateNewValue(UUID uuid) {
        CompletableFuture<Optional<BigDecimal>> future = new CompletableFuture<>();
        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
            OfflinePlayer player = instance.getServer().getOfflinePlayer(uuid);
            try {
                String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                future.complete(Optional.of(new BigDecimal(parsed)));
            } catch (Exception e) {
                future.complete(Optional.empty());
            }
        });
        return future;
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
