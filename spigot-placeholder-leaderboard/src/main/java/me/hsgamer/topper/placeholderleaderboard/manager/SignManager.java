package me.hsgamer.topper.placeholderleaderboard.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.placeholderleaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.block.BlockEntryConfig;
import me.hsgamer.topper.spigot.block.BlockManager;
import me.hsgamer.topper.spigot.number.NumberFormatter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SignManager extends BlockManager<TopperPlaceholderLeaderboard, Double> {
    public SignManager(TopperPlaceholderLeaderboard plugin) {
        super(plugin);
    }

    @Override
    protected BlockEntryConfig getConfig() {
        return ConfigGenerator.newInstance(BlockEntryConfig.class, new BukkitConfig(plugin, "sign.yml"));
    }

    @Override
    protected void updateBlock(String holderName, Block block, UUID uuid, Double value, int index) {
        BlockState blockState = block.getState();
        if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            NumberFormatter formatter = plugin.get(TopManager.class).getTopFormatter(holderName);
            String[] lines = getSignLines(uuid, value, index, formatter);
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update(false, false);
        } else {
            remove(block.getLocation());
        }
    }

    @Override
    protected void onBreak(Player player, Location location) {
        MessageUtils.sendMessage(player, plugin.get(MessageConfig.class).getSignRemoved());
    }

    @Override
    protected boolean canBreak(Player player, Location location) {
        return player.hasPermission(Permissions.SIGN_BREAK);
    }

    @Override
    protected Optional<DataEntry<Double>> getEntry(BlockEntry blockEntry) {
        return plugin.get(TopManager.class).getTopHolder(blockEntry.holderName)
                .map(NumberTopHolder::getSnapshotAgent)
                .flatMap(snapshotAgent -> snapshotAgent.getEntryByIndex(blockEntry.index));
    }

    private String[] getSignLines(UUID uuid, Double value, int index, NumberFormatter formatter) {
        List<String> list = new ArrayList<>(plugin.get(MessageConfig.class).getSignLines());
        list.replaceAll(s -> formatter.replace(s, uuid, value).replace("{index}", String.valueOf(index + 1)));
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = ColorUtils.colorize(i < list.size() ? list.get(i) : "");
        }
        return lines;
    }
}
