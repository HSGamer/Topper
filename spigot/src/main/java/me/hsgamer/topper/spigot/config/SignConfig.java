package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.AdvancedConfigPath;
import me.hsgamer.hscore.config.path.impl.IntegerConfigPath;
import me.hsgamer.hscore.config.path.impl.SimpleConfigPath;
import me.hsgamer.topper.spigot.block.BlockEntry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SignConfig extends PathableConfig {
    public static final SimpleConfigPath<List<String>> SIGN_LINES = new SimpleConfigPath<>("sign-lines", Arrays.asList(
            "&6&m               ",
            "&b#{index} &a{name}",
            "&a{value} {suffix}",
            "&6&m               "
    ));
    public static final IntegerConfigPath START_INDEX = new IntegerConfigPath("start-index", 1);
    public static final AdvancedConfigPath<List<String>, List<BlockEntry>> SIGN_ENTRIES = new AdvancedConfigPath<List<String>, List<BlockEntry>>("sign-entries", Collections.emptyList()) {
        @Override
        public @NotNull List<String> getFromConfig(@NotNull Config config) {
            Object raw = config.get(getPath());
            if (raw == null) {
                return Collections.emptyList();
            } else {
                return CollectionUtils.createStringListFromObject(raw, true);
            }
        }

        @Override
        public @NotNull List<BlockEntry> convert(@NotNull List<String> rawValue) {
            return rawValue.stream().map(BlockEntry::deserialize).collect(Collectors.toList());
        }

        @Override
        public @NotNull List<String> convertToRaw(@NotNull List<BlockEntry> value) {
            return value.stream().map(BlockEntry::serialize).collect(Collectors.toList());
        }
    };

    public SignConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "sign.yml"));
    }
}
