package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import org.bukkit.plugin.Plugin;

public class MessageConfig extends PathableConfig {
    public static final StringConfigPath PREFIX = new StringConfigPath("prefix", "&7[&bTopper&7] &r");
    public static final StringConfigPath SUCCESS = new StringConfigPath("success", "&aSuccess");
    public static final StringConfigPath PLAYER_ONLY = new StringConfigPath("player-only", "&cYou should be a player to do this");
    public static final StringConfigPath NUMBER_REQUIRED = new StringConfigPath("number-required", "&cNumber is required");
    public static final StringConfigPath TOP_HOLDER_NOT_FOUND = new StringConfigPath("top-holder-not-found", "&cThe top holder is not found");
    public static final StringConfigPath SIGN_REMOVED = new StringConfigPath("sign-removed", "&aThe sign is removed");
    public static final StringConfigPath SIGN_REQUIRED = new StringConfigPath("sign-required", "&cA sign is required");

    public MessageConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "messages.yml"));
    }
}
