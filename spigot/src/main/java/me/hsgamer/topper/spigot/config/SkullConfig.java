package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.topper.spigot.config.path.BlockEntryConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;

public class SkullConfig extends PathableConfig {
    public static final BlockEntryConfigPath SKULL_ENTRIES = new BlockEntryConfigPath("skull-entries", Collections.emptyList());

    public SkullConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "skull.yml"));
    }
}
