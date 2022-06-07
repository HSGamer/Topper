package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class BaseMainConfig extends PathableConfig {
    public static final StringConfigPath NULL_DISPLAY_NAME = new StringConfigPath("null-display-name", "---");
    public static final StringConfigPath NULL_DISPLAY_VALUE = new StringConfigPath("null-display-value", "---");
    public static final StringConfigPath STORAGE_TYPE = new StringConfigPath("storage-type", "yaml");

    public BaseMainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
