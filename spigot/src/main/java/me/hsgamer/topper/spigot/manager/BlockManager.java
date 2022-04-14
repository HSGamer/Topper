package me.hsgamer.topper.spigot.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopFormatter;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.config.path.BlockEntryConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class BlockManager implements Listener {
    protected final TopperPlugin instance;
    private final BlockEntryConfigPath configPath;
    private final List<BlockEntry> entries = new ArrayList<>();
    private BukkitTask task;

    protected BlockManager(TopperPlugin instance) {
        this.instance = instance;
        BukkitConfig config = getConfig();
        config.setup();
        this.configPath = new BlockEntryConfigPath(getEntriesPath(), Collections.emptyList());
        configPath.setConfig(config);
        config.save();
    }

    protected abstract BukkitConfig getConfig();

    protected abstract String getEntriesPath();

    protected abstract void updateBlock(Block block, UUID uuid, Double value, int index, TopFormatter formatter);

    protected abstract String getBreakMessage();

    protected abstract Permission getBreakPermission();

    public void register() {
        this.registerEvents();
        entries.addAll(configPath.getValue());
        task = Bukkit.getScheduler().runTaskTimer(instance, () -> entries.forEach(this::update), 20L, 20L);
    }

    public void unregister() {
        task.cancel();
        HandlerList.unregisterAll(this);
        configPath.setAndSave(entries);
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvent(BlockBreakEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof BlockBreakEvent)) {
                return;
            }
            BlockBreakEvent event = (BlockBreakEvent) e;
            Block block = event.getBlock();
            Location location = block.getLocation();
            Player player = event.getPlayer();
            if (!contains(location)) {
                return;
            }
            if (!player.hasPermission(getBreakPermission()) || !player.isSneaking()) {
                event.setCancelled(true);
                return;
            }
            remove(location);
            MessageUtils.sendMessage(player, getBreakMessage());
        }, instance, true);
        Bukkit.getPluginManager().registerEvent(BlockPhysicsEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof BlockPhysicsEvent)) {
                return;
            }
            BlockPhysicsEvent event = (BlockPhysicsEvent) e;
            if (contains(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }, instance, true);
        Bukkit.getPluginManager().registerEvent(BlockExplodeEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof BlockExplodeEvent)) {
                return;
            }
            BlockExplodeEvent event = (BlockExplodeEvent) e;
            event.blockList().removeIf(block -> contains(block.getLocation()));
        }, instance, true);
        Bukkit.getPluginManager().registerEvent(EntityExplodeEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof EntityExplodeEvent)) {
                return;
            }
            EntityExplodeEvent event = (EntityExplodeEvent) e;
            event.blockList().removeIf(block -> contains(block.getLocation()));
        }, instance, true);
    }

    private void update(BlockEntry entry) {
        Optional<TopHolder> optional = instance.getTopManager().getTopHolder(entry.topHolderName);
        if (!optional.isPresent()) return;
        TopHolder topHolder = optional.get();
        TopFormatter formatter = instance.getTopManager().getTopFormatter(entry.topHolderName);
        Optional<TopEntry> optionalEntry = topHolder.getEntryByIndex(entry.index);
        UUID uuid = optionalEntry.map(TopEntry::getUuid).orElse(null);
        Double value = optionalEntry.map(TopEntry::getValue).orElse(null);

        Block block = entry.location.getBlock();
        if (!block.getChunk().isLoaded()) return;
        updateBlock(block, uuid, value, entry.index, formatter);
    }

    public void add(BlockEntry entry) {
        remove(entry.location);
        entries.add(entry);
    }

    public void remove(Location location) {
        entries.removeIf(topSign -> topSign.location.equals(location));
    }

    public boolean contains(Location location) {
        return entries.stream().anyMatch(topSign -> topSign.location.equals(location));
    }
}
