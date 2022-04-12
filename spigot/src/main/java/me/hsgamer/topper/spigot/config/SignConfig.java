package me.hsgamer.topper.spigot.config;

import me.hsgamer.hscore.bukkit.config.BukkitConfig;
import me.hsgamer.hscore.config.PathableConfig;
import me.hsgamer.hscore.config.path.impl.SimpleConfigPath;
import me.hsgamer.topper.spigot.config.path.BlockEntryConfigPath;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SignConfig extends PathableConfig {
    public static final SimpleConfigPath<List<String>> SIGN_LINES = new SimpleConfigPath<>("sign-lines", Arrays.asList(
            "&6&m               ",
            "&b#{index} &a{name}",
            "&a{value} {suffix}",
            "&6&m               "
    ));
    public static final BlockEntryConfigPath SIGN_ENTRIES = new BlockEntryConfigPath("sign-entries", Collections.emptyList());

    public SignConfig(Plugin plugin) {
        super(new BukkitConfig(plugin, "sign.yml"));
    }
}
