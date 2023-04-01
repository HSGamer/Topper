package me.hsgamer.topper.placeholderleaderboard.manager;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.bukkit.utils.ColorUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.proxy.ConfigGenerator;
import me.hsgamer.topper.placeholderleaderboard.Permissions;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.spigot.config.BlockEntryConfig;
import me.hsgamer.topper.spigot.formatter.NumberFormatter;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignManager extends BlockManager {
    public SignManager(TopperPlaceholderLeaderboard instance) {
        super(instance);
    }

    @Override
    protected BlockEntryConfig getConfig() {
        Config config = new BukkitConfig(instance, "sign.yml");
        config.setup();
        if (config.contains("sign-entries")) {
            config.set("entries", config.get("sign-entries"));
            config.set("sign-entries", null);
            config.save();
        }
        return ConfigGenerator.newInstance(BlockEntryConfig.class, config, false);
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
        return instance.getMessageConfig().getSignRemoved();
    }

    @Override
    protected Permission getBreakPermission() {
        return Permissions.SIGN_BREAK;
    }

    private String[] getSignLines(UUID uuid, Double value, int index, NumberFormatter formatter) {
        List<String> list = new ArrayList<>(instance.getMessageConfig().getSignLines());
        list.replaceAll(s -> formatter.replace(s, uuid, value).replace("{index}", String.valueOf(index + 1)));
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = ColorUtils.colorize(i < list.size() ? list.get(i) : "");
        }
        return lines;
    }
}
