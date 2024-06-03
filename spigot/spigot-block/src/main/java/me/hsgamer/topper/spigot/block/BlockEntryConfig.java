package me.hsgamer.topper.spigot.block;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.annotation.ConfigPath;

public interface BlockEntryConfig {
    @ConfigPath("entries")
    default BlockEntry[] getEntries() {
        return new BlockEntry[0];
    }

    void setEntries(BlockEntry[] entries);

    void reloadConfig();

    Config getConfig();
}
