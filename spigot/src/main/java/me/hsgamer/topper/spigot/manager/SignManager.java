package me.hsgamer.topper.spigot.manager;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.config.SignConfig;
import me.hsgamer.topper.spigot.sign.SignEntry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class SignManager implements Listener {
    private final TopperPlugin instance;
    private final List<SignEntry> signEntries = new ArrayList<>();
    private BukkitTask task;

    public SignManager(TopperPlugin instance) {
        this.instance = instance;
    }

    public void register() {
        instance.registerListener(this);
        signEntries.addAll(SignConfig.SIGN_ENTRIES.getValue());
        task = Bukkit.getScheduler().runTaskTimer(instance, () -> signEntries.forEach(SignEntry::update), 20L, 20L);
    }

    public void unregister() {
        task.cancel();
        HandlerList.unregisterAll(this);
        SignConfig.SIGN_ENTRIES.setValue(signEntries);
    }

    public void addSign(SignEntry entry) {
        removeSign(entry.location);
        signEntries.add(entry);
    }

    public void removeSign(Location location) {
        signEntries.removeIf(topSign -> topSign.location.equals(location));
    }

    public boolean containsSign(Location location) {
        return signEntries.stream().anyMatch(topSign -> topSign.location.equals(location));
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        Player player = event.getPlayer();
        if (containsSign(location)) {
            if (!player.hasPermission(Permissions.SIGN_BREAK) || !player.isSneaking()) {
                event.setCancelled(true);
                return;
            }
            removeSign(location);
            MessageUtils.sendMessage(player, MessageConfig.SIGN_REMOVED.getValue());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPhysics(BlockPhysicsEvent event) {
        if (containsSign(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> containsSign(block.getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> containsSign(block.getLocation()));
    }
}
