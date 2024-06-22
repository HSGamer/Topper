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

    void reloadConfig();
}
