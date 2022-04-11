package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.hscore.config.path.BaseConfigPath;
import me.hsgamer.hscore.config.path.impl.StringConfigPath;
import me.hsgamer.topper.core.TopFormatter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
    public static final AdvancedConfigPath<Map<String, Map<String, Object>>, Map<String, TopFormatter>> FORMATTERS = new AdvancedConfigPath<Map<String, Map<String, Object>>, Map<String, TopFormatter>>("formatters", Collections.emptyMap()) {
        @Override
        public @NotNull Map<String, Map<String, Object>> getFromConfig(@NotNull Config config) {
            Map<String, Map<String, Object>> map = new HashMap<>();
            config.getKeys(getPath(), false).forEach(key -> map.put(key, config.getValues(getPath() + "." + key, false)));
            return map;
        }

        @Override
        public @NotNull Map<String, TopFormatter> convert(@NotNull Map<String, Map<String, Object>> rawValue) {
            Map<String, TopFormatter> map = new HashMap<>();
            rawValue.forEach((key, value) -> map.put(key, new TopFormatter(value)));
            return map;
        }

        @Override
        public @NotNull Map<String, Map<String, Object>> convertToRaw(@NotNull Map<String, TopFormatter> value) {
            Map<String, Map<String, Object>> map = new HashMap<>();
            value.forEach((key, formatter) -> map.put(key, formatter.toMap()));
            return map;
        }
    };
    public static final StringConfigPath STORAGE_TYPE = new StringConfigPath("storage-type", "yaml");
    public static final StringConfigPath NULL_DISPLAY_NAME = new StringConfigPath("null-display-name", "---");
    public static final StringConfigPath NULL_DISPLAY_VALUE = new StringConfigPath("null-display-value", "---");

    public MainConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "config.yml"));
    }
}
