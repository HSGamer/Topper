package me.hsgamer.topper.spigot.manager;

import me.hsgamer.hscore.config.path.ConfigPath;
import me.hsgamer.topper.core.TopFormatter;
import me.hsgamer.topper.spigot.Permissions;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.block.BlockEntry;
import me.hsgamer.topper.spigot.config.MessageConfig;
import me.hsgamer.topper.spigot.config.SkullConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.permissions.Permission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SkullManager extends BlockManager {
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

    public SkullManager(TopperPlugin instance) {
        super(instance);
    }

    @Override
    protected void updateBlock(Block block, UUID uuid, Double value, int index, TopFormatter formatter) {
        OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(uuid == null ? skullUUID : uuid);
        BlockState blockState = block.getState();
        if (blockState instanceof Skull) {
            Skull skull = (Skull) blockState;
            setOwner(skull, topPlayer);
            skull.update(false, false);
        }
    }

    private void setOwner(Skull skull, OfflinePlayer owner) {
        if (setOwningPlayerMethod != null) {
            try {
                setOwningPlayerMethod.invoke(skull, owner);
            } catch (IllegalAccessException | InvocationTargetException e) {
                instance.getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        } else {
            try {
                setOwnerMethod.invoke(skull, owner == null ? null : owner.getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                instance.getLogger().log(Level.WARNING, "Error when setting owner for skulls", e);
            }
        }
    }

    @Override
    protected ConfigPath<List<BlockEntry>> getEntriesConfigPath() {
        return SkullConfig.SKULL_ENTRIES;
    }

    @Override
    protected String getBreakMessage() {
        return MessageConfig.SKULL_REMOVED.getValue();
    }

    @Override
    protected Permission getBreakPermission() {
        return Permissions.SIGN_BREAK;
    }
}
