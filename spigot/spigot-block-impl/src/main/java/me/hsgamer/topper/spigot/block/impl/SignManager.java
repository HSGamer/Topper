package me.hsgamer.topper.spigot.block.impl;

import me.hsgamer.topper.spigot.block.BlockManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

public abstract class SignManager<P extends Plugin, K, V> extends BlockManager<P, K, V> {
    public SignManager(P plugin) {
        super(plugin);
    }

    @Override
    protected void updateBlock(String holderName, Block block, K key, V value, int index) {
        BlockState blockState = block.getState();
        if (blockState instanceof Sign) {
            Sign sign = (Sign) blockState;
            String[] lines = getSignLines(key, value, index, holderName);
            for (int i = 0; i < 4; i++) {
                sign.setLine(i, lines[i]);
            }
            sign.update(false, false);
        } else {
            remove(block.getLocation());
        }
    }

    protected abstract String[] getSignLines(K key, V value, int index, String holderName);
}
