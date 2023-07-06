package me.hsgamer.topper.placeholderleaderboard.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.topper.spigot.number.NumberFormatter;

import java.util.Collections;
import java.util.Map;

public interface MainConfig {
    @ConfigPath("placeholders")
    default Map<String, String> getPlaceholders() {
        return Collections.emptyMap();
    }

    @ConfigPath("formatters")
    default Map<String, NumberFormatter> getFormatters() {
        return Collections.emptyMap();
    }

    @ConfigPath("load-all-offline-players")
    default boolean isLoadAllOfflinePlayers() {
        return false;
    }

    @ConfigPath({"task", "save", "entry-per-tick"})
    default int getTaskSaveEntryPerTick() {
        return 10;
    }

    @ConfigPath({"task", "save", "delay"})
    default int getTaskSaveDelay() {
        return 0;
    }

    @ConfigPath({"task", "update", "entry-per-tick"})
    default int getTaskUpdateEntryPerTick() {
        return 10;
    }

    @ConfigPath({"task", "update", "delay"})
    default int getTaskUpdateDelay() {
        return 0;
    }

    @ConfigPath("null-display-name")
    default String getNullDisplayName() {
        return "---";
    }

    @ConfigPath("null-display-value")
    default String getNullDisplayValue() {
        return "---";
    }

    @ConfigPath("storage-type")
    default String getStorageType() {
        return "yaml";
    }

    void reloadConfig();
}
