package me.hsgamer.topper.placeholderleaderboard.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

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

    void reloadConfig();
}
