package me.hsgamer.topper.spigot.plugin.config;

import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.topper.spigot.plugin.config.converter.StringValueMapConverter;

import java.util.Collections;
import java.util.Map;

public interface MainConfig {
    @ConfigPath(value = "placeholders", converter = StringValueMapConverter.class, priority = 1)
    @Comment({
            "This is where you add placeholders that the plugin will listen for values to store in the leaderboard",
            "Note that the placeholder should have its final value as a number (or else the leaderboard won't update)",
            "",
            "You can add by adding to the section with the format \"<name>: <placeholder>\"",
            "This creates a new \"Top Holder\" with the name \"<name>\" and the placeholder \"<placeholder>\"",
            "You can add \"[ONLINE]\" before the \"<placeholder>\" to indicate that the holder should only get values when the player is online",
            "You can add \"[ASYNC]\" before the \"<placeholder>\" to indicate that the holder should get values asynchronously",
            "You can add \"[LENIENT]\" before the \"<placeholder>\" to indicate that the holder should ignore errors while parsing the placeholder. ONLY USE THIS IF YOU ARE SURE THAT THE ERRORS ARE NOT CRITICAL",
            "",
            "For example, If you want to create three new leaderboards",
            "- \"player_x\" listens to the placeholder \"%player_x%\"",
            "- \"player_y\" listens to the placeholder \"%player_y%\" only when the players are online",
            "- \"player_z\" listens to the placeholder \"%player_z%\" only when the players are online and get values asynchronously",
            "Here is how it's set",
            "placeholders:",
            "  player_x: '%player_x%'",
            "  player_y: '[ONLINE] %player_y%' add [ONLINE] before %player_y% to specify that this holder only gets the value from the placeholder when the player is online",
            "  player_z: '[ASYNC][ONLINE] %player_z%' add [ONLINE] before %player_z% to specify that this holder only gets the value from the placeholder when the player is online, and it should get values asynchronously"
    })
    default Map<String, String> getPlaceholders() {
        return Collections.emptyMap();
    }

    @ConfigPath(value = "load-all-offline-players", priority = 3)
    @Comment("Should the plugin load all offline players when the server starts")
    default boolean isLoadAllOfflinePlayers() {
        return false;
    }

    @ConfigPath(value = {"task", "save", "entry-per-tick"}, priority = 4)
    @Comment("How many entries should be saved per tick")
    default int getTaskSaveEntryPerTick() {
        return 10;
    }

    @ConfigPath(value = {"task", "save", "delay"}, priority = 4)
    @Comment("How many ticks should the plugin wait before saving the leaderboard")
    default int getTaskSaveDelay() {
        return 0;
    }

    @ConfigPath(value = {"task", "update", "entry-per-tick"}, priority = 5)
    @Comment("How many entries should be updated per tick")
    default int getTaskUpdateEntryPerTick() {
        return 10;
    }

    @ConfigPath(value = {"task", "update", "delay"}, priority = 5)
    @Comment("How many ticks should the plugin wait before updating the leaderboard")
    default int getTaskUpdateDelay() {
        return 0;
    }

    @ConfigPath(value = "storage-type")
    @Comment({
            "The type of storage the plugin will use to store the value",
            "Available: YAML, JSON, SQLITE, MYSQL"
    })
    default String getStorageType() {
        return "yaml";
    }

    void reloadConfig();
}
