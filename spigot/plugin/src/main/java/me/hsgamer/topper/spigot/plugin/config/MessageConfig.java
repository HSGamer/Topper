package me.hsgamer.topper.spigot.plugin.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface MessageConfig {
    @ConfigPath("prefix")
    default String getPrefix() {
        return "&7[&bTopper&7] &r";
    }

    @ConfigPath("success")
    default String getSuccess() {
        return "&aSuccess";
    }

    @ConfigPath("number-required")
    default String getNumberRequired() {
        return "&cNumber is required";
    }

    @ConfigPath("illegal-from-to-index")
    default String getIllegalFromToIndex() {
        return "&cThe from index should be less than the to index";
    }

    @ConfigPath("top-entry-line")
    default String getTopEntryLine() {
        return "&7[&b{index}&7] &b{name} &7- &b{value}";
    }

    @ConfigPath("top-empty")
    default String getTopEmpty() {
        return "&cNo top entry";
    }

    @ConfigPath("top-holder-not-found")
    default String getTopHolderNotFound() {
        return "&cThe top holder is not found";
    }

    @ConfigPath({"display", "null-uuid"})
    default String getDisplayNullUuid() {
        return "---";
    }

    @ConfigPath({"display", "null-value"})
    default String getDisplayNullValue() {
        return "---";
    }

    @ConfigPath({"display", "null-name"})
    default String getDisplayNullName() {
        return "---";
    }

    @ConfigPath({"display", "number-format"})
    default String getNumberFormat() {
        return "#.##";
    }

    void reloadConfig();

    default String getDisplayUuid(@Nullable UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(UUID::toString)
                .orElse(getDisplayNullUuid());
    }

    default String getDisplayName(@Nullable UUID uuid) {
        return Optional.ofNullable(uuid)
                .map(Bukkit::getOfflinePlayer)
                .map(OfflinePlayer::getName)
                .orElse(getDisplayNullName());
    }

    default String getDisplayValue(@Nullable Double value) {
        return Optional.ofNullable(value)
                .map(new DecimalFormat(getNumberFormat())::format)
                .orElse(getDisplayNullValue());
    }

    default String getDisplayRawValue(@Nullable Double value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .orElse(getDisplayNullValue());
    }

    default String getTopEntryLine(int index, @Nullable Map.Entry<UUID, Double> dataSnapshot) {
        return getTopEntryLine()
                .replace("{index}", String.valueOf(index))
                .replace("{uuid}", getDisplayUuid(dataSnapshot == null ? null : dataSnapshot.getKey()))
                .replace("{name}", getDisplayName(dataSnapshot == null ? null : dataSnapshot.getKey()))
                .replace("{value}", getDisplayValue(dataSnapshot == null ? null : dataSnapshot.getValue()))
                .replace("{raw_value}", getDisplayRawValue(dataSnapshot == null ? null : dataSnapshot.getValue()));
    }
}
