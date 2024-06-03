package me.hsgamer.topper.placeholderleaderboard.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.placeholderleaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.block.BlockEntryConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class SkullManager extends me.hsgamer.topper.spigot.block.impl.SkullManager<TopperPlaceholderLeaderboard, UUID, Double> {
    public SkullManager(TopperPlaceholderLeaderboard plugin) {
        super(plugin);
    }

    @Override
    protected BlockEntryConfig getConfig() {
        return ConfigGenerator.newInstance(BlockEntryConfig.class, new BukkitConfig(plugin, "skull.yml"));
    }

    @Override
    protected void onBreak(Player player, Location location) {
        MessageUtils.sendMessage(player, plugin.get(MessageConfig.class).getSkullRemoved());
    }

    @Override
    protected boolean canBreak(Player player, Location location) {
        return player.hasPermission(Permissions.SKULL_BREAK);
    }

    @Override
    protected Optional<UUID> getOwner(UUID uuid, Double value, int index) {
        return Optional.of(uuid);
    }

    @Override
    protected Optional<DataEntry<UUID, Double>> getEntry(BlockEntry blockEntry) {
        return plugin.get(TopManager.class).getTopHolder(blockEntry.holderName)
                .map(NumberTopHolder::getSnapshotAgent)
                .flatMap(snapshotAgent -> snapshotAgent.getEntryByIndex(blockEntry.index));
    }
}
