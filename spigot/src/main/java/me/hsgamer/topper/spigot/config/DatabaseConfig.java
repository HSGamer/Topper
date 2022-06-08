package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.BooleanConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class DatabaseConfig extends PathableConfig {
    public static final StringConfigPath DATA_FOLDER = new StringConfigPath("data-folder", "top");
    public static final StringConfigPath HOST = new StringConfigPath("host", "localhost");
    public static final StringConfigPath PORT = new StringConfigPath("port", "3306");
    public static final StringConfigPath DATABASE = new StringConfigPath("database", "topper");
    public static final StringConfigPath USERNAME = new StringConfigPath("username", "root");
    public static final StringConfigPath PASSWORD = new StringConfigPath("password", "");
    public static final BooleanConfigPath USE_SSL = new BooleanConfigPath("use-ssl", false);

    public DatabaseConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "database.yml"));
    }
}
