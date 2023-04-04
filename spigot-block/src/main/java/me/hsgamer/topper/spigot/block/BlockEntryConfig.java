package me.hsgamer.topper.spigot.block;

import me.hsgamer.hscore.config.Config;
import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.util.Collections;
import java.util.List;

public interface BlockEntryConfig {
    @ConfigPath("entries")
    default List<BlockEntry> getEntries() {
        return Collections.emptyList();
    }

    void setEntries(List<BlockEntry> entries);

    void reloadConfig();

    Config getConfig();
}
