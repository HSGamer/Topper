package me.hsgamer.topper.spigot.block.impl;

import me.hsgamer.topper.spigot.block.BlockManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public abstract class SkullManager<P extends Plugin, K, V> extends BlockManager<P, K, V> {
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

    public SkullManager(P plugin) {
        super(plugin);
    }

    protected abstract Optional<UUID> getOwner(K key, V value, int index);

    @Override
    protected void updateBlock(String holderName, Block block, K key, V value, int index) {
        UUID uuid = Optional.ofNullable(key)
                .flatMap(k -> getOwner(key, value, index))
                .orElse(skullUUID);
        OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(uuid);
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
}
