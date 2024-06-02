package me.hsgamer.topper.spigot.block;

import io.github.projectunified.minelib.plugin.base.Loadable;
import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.common.task.Task;
import io.github.projectunified.minelib.scheduler.location.LocationScheduler;
import me.hsgamer.topper.core.entry.DataEntry;
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
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BlockManager<P extends Plugin, T> implements Listener, Loadable {
    protected final P plugin;
    private final BlockEntryConfig blockEntryConfig;
    private final Map<Location, BlockEntry> entries = new HashMap<>();
    private Task task;

    protected BlockManager(P plugin) {
        this.plugin = plugin;
        this.blockEntryConfig = getConfig();
    }

    protected abstract BlockEntryConfig getConfig();

    protected abstract void updateBlock(String holderName, Block block, UUID uuid, T value, int index);

    protected abstract boolean canBreak(Player player, Location location);

    protected void onBreak(Player player, Location location) {
        // EMPTY
    }

    protected abstract Optional<DataEntry<UUID, T>> getEntry(BlockEntry blockEntry);

    @Override
    public void enable() {
        this.registerEvents();
        blockEntryConfig.getEntries().forEach(this::add);

        final Queue<BlockEntry> entryQueue = new LinkedList<>();
        final AtomicBoolean isBlockUpdating = new AtomicBoolean(false);
        task = AsyncScheduler.get(plugin).runTimer(() -> {
            if (isBlockUpdating.get()) return;

            if (entryQueue.isEmpty()) {
                entryQueue.addAll(this.entries.values());
                return;
            }
            BlockEntry blockEntry = entryQueue.poll();
            if (blockEntry == null) return;

            Optional<DataEntry<UUID, T>> optionalEntry = getEntry(blockEntry);
            UUID uuid = optionalEntry.map(DataEntry::getKey).orElse(null);
            T value = optionalEntry.map(DataEntry::getValue).orElse(null);

            isBlockUpdating.set(true);
            LocationScheduler.get(plugin, blockEntry.location).run(() -> {
                Block block = blockEntry.location.getBlock();
                if (block.getChunk().isLoaded()) {
                    updateBlock(blockEntry.holderName, block, uuid, value, blockEntry.index);
                }
                isBlockUpdating.set(false);
            });
        }, 20L, 20L);
    }

    @Override
    public void disable() {
        task.cancel();
        HandlerList.unregisterAll(this);
        blockEntryConfig.setEntries(new ArrayList<>(entries.values()));
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
            if (!player.isSneaking() || !canBreak(player, location)) {
                event.setCancelled(true);
                return;
            }
            remove(location);
            onBreak(player, location);
        }, plugin, true);
        Bukkit.getPluginManager().registerEvent(BlockPhysicsEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof BlockPhysicsEvent)) {
                return;
            }
            BlockPhysicsEvent event = (BlockPhysicsEvent) e;
            if (contains(event.getBlock().getLocation())) {
                event.setCancelled(true);
            }
        }, plugin, true);
        Bukkit.getPluginManager().registerEvent(BlockExplodeEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof BlockExplodeEvent)) {
                return;
            }
            BlockExplodeEvent event = (BlockExplodeEvent) e;
            event.blockList().removeIf(block -> contains(block.getLocation()));
        }, plugin, true);
        Bukkit.getPluginManager().registerEvent(EntityExplodeEvent.class, this, EventPriority.NORMAL, (l, e) -> {
            if (!(e instanceof EntityExplodeEvent)) {
                return;
            }
            EntityExplodeEvent event = (EntityExplodeEvent) e;
            event.blockList().removeIf(block -> contains(block.getLocation()));
        }, plugin, true);
    }

    public void add(BlockEntry entry) {
        remove(entry.location);
        entries.put(entry.location, entry);
    }

    public void remove(Location location) {
        entries.remove(location);
    }

    public boolean contains(Location location) {
        return entries.containsKey(location);
    }
}
