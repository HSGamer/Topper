package me.hsgamer.topper.placeholderleaderboard.config;

import me.hsgamer.hscore.config.annotation.Comment;
import me.hsgamer.hscore.config.annotation.ConfigPath;
import me.hsgamer.topper.spigot.number.NumberFormatter;

import java.util.Collections;
import java.util.Map;

public interface MainConfig {
    @ConfigPath("placeholders")
    @Comment({
            "This is where you add placeholders that the plugin will listen for values to store in the leaderboard",
            "Note that the placeholder should have its final value as a number (or else the leaderboard won't update)",
            "",
            "You can add by adding to the section with the format \"<name>: <placeholder>\"",
            "This creates a new \"Top Holder\" with the name \"<name>\" and the placeholder \"<placeholder>\"",
            "You can add \"[ONLINE]\" before the \"<placeholder>\" to indicate that the holder should only get values when the player is online",
            "You can add \"[ASYNC]\" before the \"<placeholder>\" to indicate that the holder should get values asynchronously",
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

    @ConfigPath("formatters")
    @Comment({
            "The formatters to display the value in the leaderboard",
            "This modifies how the value is shown in the leaderboard provided by the Top Holder",
            "You have to specify the Top Holder's name and formatting settings for it",
            "But you don't need to set all options, just use some necessary ones",
            "",
            "formatters:",
            "  player_x: # The Top Holder's name",
            "    display-name: \"Player X\" # The display name of the leaderboard",
            "    prefix: \"X \" # The prefix before the value",
            "    suffix: \" pt\" # The suffix after the value",
            "    fraction-digits: 2 # How many digits after the decimal separator will be shown",
            "    decimal-separator: \".\" # The decimal separator between the integer part and the fractional part of the value",
            "    group-separator: \",\" # The separator between each three digits of the integer part",
            "    show-group-separator: false # Should the group separator be shown in the formatted value",
            "    null-display-uuid: \"N\\A\" # The UUID to display when the player is not found",
            "    null-display-name: \"N\\A\" # The name to display when the player is not found",
            "    null-display-value: \"N\\A\" # The value to display when the player is not found",
    })
    default Map<String, NumberFormatter> getFormatters() {
        return Collections.emptyMap();
    }

    @ConfigPath("load-all-offline-players")
    @Comment("Should the plugin load all offline players when the server starts")
    default boolean isLoadAllOfflinePlayers() {
        return false;
    }

    @ConfigPath({"task", "save", "entry-per-tick"})
    @Comment("How many entries should be saved per tick")
    default int getTaskSaveEntryPerTick() {
        return 10;
    }

    @ConfigPath({"task", "save", "delay"})
    @Comment("How many ticks should the plugin wait before saving the leaderboard")
    default int getTaskSaveDelay() {
        return 0;
    }

    @ConfigPath({"task", "update", "entry-per-tick"})
    @Comment("How many entries should be updated per tick")
    default int getTaskUpdateEntryPerTick() {
        return 10;
    }

    @ConfigPath({"task", "update", "delay"})
    @Comment("How many ticks should the plugin wait before updating the leaderboard")
    default int getTaskUpdateDelay() {
        return 0;
    }

    @ConfigPath("storage-type")
    @Comment({
            "The type of storage the plugin will use to store the value",
            "Available: YAML, SQLITE, MYSQL"
    })
    default String getStorageType() {
        return "yaml";
    }

    void reloadConfig();
}
