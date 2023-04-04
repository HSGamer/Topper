package me.hsgamer.topper.spigot.placeholderleaderboard.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.block.BlockEntryConfig;
import me.hsgamer.topper.spigot.block.BlockManager;
import me.hsgamer.topper.spigot.placeholderleaderboard.Permissions;
import me.hsgamer.topper.spigot.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.spigot.placeholderleaderboard.holder.NumberTopHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class SkullManager extends BlockManager<TopperPlaceholderLeaderboard, Double> {
    private static final UUID skullUUID = UUID.fromString("832b9c2d-f6c2-4c5f-8e0d-e7e7c4e9f9c8");
    private static Method setOwningPlayerMethod;
    private static Method setOwnerMethod;

    static {
        try {
            setOwnerMethod = Skull.class.getDeclaredMethod("setOwner", String.class);
            setOwningPlayerMethod = Skull.class.getDeclaredMethod("setOwningPlayer", OfflinePlayer.class);
        } catch (NoSuchMethodException e) {
            // IGNORED
        }
    }

    public SkullManager(TopperPlaceholderLeaderboard plugin) {
        super(plugin);
    }

    @Override
    protected BlockEntryConfig getConfig() {
        Config config = new BukkitConfig(plugin, "skull.yml");
        config.setup();
        if (config.contains("skull-entries")) {
            config.set("entries", config.get("skull-entries"));
            config.set("skull-entries", null);
            config.save();
        }
        return ConfigGenerator.newInstance(BlockEntryConfig.class, config, false);
    }

    @Override
    protected void updateBlock(String holderName, Block block, UUID uuid, Double value, int index) {
        OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(uuid == null ? skullUUID : uuid);
        BlockState blockState = block.getState();
        if (blockState instanceof Skull) {
            Skull skull = (Skull) blockState;
            setOwner(skull, topPlayer);
            skull.update(false, false);
        }
    }

    @Override
    protected void onBreak(Player player, Location location) {
        MessageUtils.sendMessage(player, plugin.getMessageConfig().getSkullRemoved());
    }

    @Override
    protected boolean canBreak(Player player, Location location) {
        return player.hasPermission(Permissions.SKULL_BREAK);
    }

    private void setOwner(Skull skull, OfflinePlayer owner) {
        if (setOwningPlayerMethod != null) {
            try {
                setOwningPlayerMethod.invoke(skull, owner);
            } catch (IllegalAccessException | InvocationTargetException e) {
                plugin.getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        } else {
            try {
                setOwnerMethod.invoke(skull, owner == null ? null : owner.getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                plugin.getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        }
    }

    @Override
    protected Optional<DataEntry<Double>> getEntry(BlockEntry blockEntry) {
        return plugin.getTopManager().getTopHolder(blockEntry.holderName)
                .map(NumberTopHolder::getSnapshotAgent)
                .flatMap(snapshotAgent -> snapshotAgent.getEntryByIndex(blockEntry.index));
    }
}
