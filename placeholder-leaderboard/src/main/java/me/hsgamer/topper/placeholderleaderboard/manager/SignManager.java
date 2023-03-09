package me.hsgamer.topper.placeholderleaderboard.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MessageConfig;
import me.hsgamer.topper.spigot.formatter.NumberFormatter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.permissions.Permission;

import java.util.List;
import java.util.UUID;

public class SignManager extends BlockManager {
    public SignManager(TopperPlaceholderLeaderboard instance) {
        super(instance);
    }

    @Override
    protected BukkitConfig getConfig() {
        return new BukkitConfig(instance, "sign.yml");
    }

    @Override
    protected String getEntriesPath() {
        return "sign-entries";
    }

    @Override
    protected void updateBlock(Block block, UUID uuid, Double value, int index, NumberFormatter formatter) {
        BlockState blockState = block.getState();
        if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
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
    protected String getBreakMessage() {
        return MessageConfig.SIGN_REMOVED.getValue();
    }

    @Override
    protected Permission getBreakPermission() {
        return Permissions.SIGN_BREAK;
    }

    private String[] getSignLines(UUID uuid, Double value, int index, NumberFormatter formatter) {
        List<String> list = MessageConfig.SIGN_LINES.getValue();
        list.replaceAll(s -> formatter.replace(s, uuid, value).replace("{index}", String.valueOf(index + 1)));
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = MessageUtils.colorize(i < list.size() ? list.get(i) : "");
        }
        return lines;
    }
}
