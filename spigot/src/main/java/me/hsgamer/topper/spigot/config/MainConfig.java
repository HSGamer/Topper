package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.BaseConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainConfig extends PathableConfig {
    public static final BaseConfigPath<Map<String, String>> PLACEHOLDERS = new BaseConfigPath<>("placeholders", Collections.emptyMap(), o -> {
        Map<String, String> map = new HashMap<>();
        if (o instanceof Map) {
            ((Map<?, ?>) o).forEach((key, value) -> map.put(Objects.toString(key), Objects.toString(value)));
        }
        return map;
    });

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
