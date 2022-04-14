package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.SimpleConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import me.hsgamer.topper.spigot.TopperPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MessageConfig extends PathableConfig {
    public static final StringConfigPath PREFIX = new StringConfigPath("prefix", "&7[&bTopper&7] &r");
    public static final StringConfigPath SUCCESS = new StringConfigPath("success", "&aSuccess");
    public static final StringConfigPath PLAYER_ONLY = new StringConfigPath("player-only", "&cYou should be a player to do this");
    public static final StringConfigPath NUMBER_REQUIRED = new StringConfigPath("number-required", "&cNumber is required");
    public static final StringConfigPath ILLEGAL_FROM_TO_INDEX = new StringConfigPath("illegal-from-to-index", "&cThe from index should be less than the to index");
    public static final StringConfigPath TOP_ENTRY_LINE = new StringConfigPath("top-entry-line", "&7[&b{index}&7] &b{name} &7- &b{value}");
    public static final StringConfigPath TOP_EMPTY = new StringConfigPath("top-empty", "&cNo top entry");
    public static final StringConfigPath TOP_HOLDER_NOT_FOUND = new StringConfigPath("top-holder-not-found", "&cThe top holder is not found");
    public static final StringConfigPath SIGN_REMOVED = new StringConfigPath("sign-removed", "&aThe sign is removed");
    public static final StringConfigPath SIGN_REQUIRED = new StringConfigPath("sign-required", "&cA sign is required");
    public static final StringConfigPath SKULL_REMOVED = new StringConfigPath("skull-removed", "&aThe skull is removed");
    public static final StringConfigPath SKULL_REQUIRED = new StringConfigPath("skull-required", "&cA skull is required");
    public static final SimpleConfigPath<List<String>> SIGN_LINES = new SimpleConfigPath<List<String>>("sign-lines", Arrays.asList(
            "&6&m               ",
            "&b#{index} &a{name}",
            "&a{value} {suffix}",
            "&6&m               "
    )) {
        @Override
        public void migrateConfig(@NotNull Config config) {
            JavaPlugin plugin = JavaPlugin.getPlugin(TopperPlugin.class);
            File file = new File(plugin.getDataFolder(), "skull.yml");
            if (file.exists()) {
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
                if (yamlConfiguration.contains("sign-lines")) {
                    config.set(getPath(), yamlConfiguration.get("sign-lines"));
                    yamlConfiguration.set("sign-lines", null);
                    try {
                        yamlConfiguration.save(file);
                    } catch (Exception ignored) {
                        // IGNORED
                    }
                }
            } else {
                super.migrateConfig(config);
            }
        }
    };

    public MessageConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "messages.yml"));
    }
}
