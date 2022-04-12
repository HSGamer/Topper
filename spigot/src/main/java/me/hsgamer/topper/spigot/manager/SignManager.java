package me.hsgamer.topper.spigot.manager;

import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.topper.core.TopFormatter;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.config.SignConfig;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.permissions.Permission;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SignManager extends BlockManager {
    public SignManager(TopperPlugin instance) {
        super(instance);
    }

    @Override
    protected void updateBlock(Block block, UUID uuid, BigDecimal value, int index, TopFormatter formatter) {
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
    protected ConfigPath<List<BlockEntry>> getEntriesConfigPath() {
        return SignConfig.SIGN_ENTRIES;
    }

    @Override
    protected String getBreakMessage() {
        return MessageConfig.SIGN_REMOVED.getValue();
    }

    @Override
    protected Permission getBreakPermission() {
        return Permissions.SIGN_BREAK;
    }

    private String[] getSignLines(UUID uuid, BigDecimal value, int index, TopFormatter formatter) {
        List<String> list = SignConfig.SIGN_LINES.getValue();
        list.replaceAll(s -> formatter.replace(s, uuid, value).replace("{index}", String.valueOf(index + 1)));
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = MessageUtils.colorize(i < list.size() ? list.get(i) : "");
        }
        return lines;
    }
}
