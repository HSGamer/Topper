package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
import me.hsgamer.hscore.config.path.impl.SimpleConfigPath;
import me.hsgamer.topper.spigot.sign.SignEntry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SignConfig extends PathableConfig {
    public static final SimpleConfigPath<List<String>> SIGN_LINES = new SimpleConfigPath<>("sign-lines", Arrays.asList(
            "&6&m               ",
            "&b#<index> &a<name>",
            "&a<value> <suffix>",
            "&6&m               "
    ));
    public static final IntegerConfigPath START_INDEX = new IntegerConfigPath("start-index", 1);
    public static final AdvancedConfigPath<List<Map<String, Object>>, List<SignEntry>> SIGN_ENTRIES = new AdvancedConfigPath<List<Map<String, Object>>, List<SignEntry>>("sign-entries", Collections.emptyList()) {
        @Override
        public @NotNull List<Map<String, Object>> getFromConfig(@NotNull Config config) {
            //noinspection unchecked
            return ((List<Map<String, Object>>) config.getNormalized(getPath()));
        }

        @Override
        public @NotNull List<SignEntry> convert(@NotNull List<Map<String, Object>> rawValue) {
            return rawValue.stream().map(SignEntry::deserialize).collect(Collectors.toList());
        }

        @Override
        public @NotNull List<Map<String, Object>> convertToRaw(@NotNull List<SignEntry> value) {
            return value.stream().map(SignEntry::serialize).collect(Collectors.toList());
        }
    };

    public SignConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "sign.yml"));
    }
}
