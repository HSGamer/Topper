package me.hsgamer.topper.placeholderleaderboard.config;

import me.hsgamer.hscore.config.annotation.ConfigPath;

import java.util.Arrays;
import java.util.List;

public interface MessageConfig {
    @ConfigPath("prefix")
    default String getPrefix() {
        return "&7[&bTopper&7] &r";
    }

    @ConfigPath("success")
    default String getSuccess() {
        return "&aSuccess";
    }

    @ConfigPath("player-only")
    default String getPlayerOnly() {
        return "&cYou should be a player to do this";
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

    @ConfigPath("sign-removed")
    default String getSignRemoved() {
        return "&aThe sign is removed";
    }

    @ConfigPath("sign-required")
    default String getSignRequired() {
        return "&cA sign is required";
    }

    @ConfigPath("skull-removed")
    default String getSkullRemoved() {
        return "&aThe skull is removed";
    }

    @ConfigPath("skull-required")
    default String getSkullRequired() {
        return "&cA skull is required";
    }

    @ConfigPath("sign-lines")
    default List<String> getSignLines() {
        return Arrays.asList(
                "&6&m               ",
                "&b#{index} &a{name}",
                "&a{value} {suffix}",
                "&6&m               "
        );
    }

    void reloadConfig();
}
