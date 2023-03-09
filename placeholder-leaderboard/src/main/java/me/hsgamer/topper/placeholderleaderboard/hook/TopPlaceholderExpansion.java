package me.hsgamer.topper.placeholderleaderboard.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.topper.core.entry.DataEntry;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.formatter.NumberFormatter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TopPlaceholderExpansion extends PlaceholderExpansion {
    private final TopperPlaceholderLeaderboard instance;

    public TopPlaceholderExpansion(TopperPlaceholderLeaderboard instance) {
        this.instance = instance;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "topper";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", instance.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.split(";");
        if (args.length < 2) return null;
        Optional<NumberTopHolder> optionalHolder = instance.getTopManager().getTopHolder(args[0]);
        if (!optionalHolder.isPresent()) return null;
        NumberTopHolder holder = optionalHolder.get();
        NumberFormatter formatter = instance.getTopManager().getTopFormatter(args[0]);

        switch (args[1]) {
            case "top_name": {
                int i = 1;
                if (args.length > 2) {
                    try {
                        i = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignored) {
                        // IGNORED
                    }
                }
                return holder.getSnapshotAgent().getEntryByIndex(i - 1)
                        .map(DataEntry::getUuid)
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .orElseGet(MainConfig.NULL_DISPLAY_NAME::getValue);
            }
            case "top_value_raw":
            case "top_value": {
                int i = 1;
                if (args.length > 2) {
                    try {
                        i = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignored) {
                        // IGNORED
                    }
                }
                return holder.getSnapshotAgent().getEntryByIndex(i - 1)
                        .map(DataEntry::getValue)
                        .map(value -> args[1].endsWith("raw") ? String.valueOf(value) : formatter.format(value))
                        .orElseGet(MainConfig.NULL_DISPLAY_VALUE::getValue);
            }
            case "top_rank":
                return Integer.toString(holder.getSnapshotAgent().getSnapshotIndex(player.getUniqueId()) + 1);
            case "value":
            case "value_raw":
                return holder.getEntry(player.getUniqueId())
                        .map(DataEntry::getValue)
                        .map(value -> args[1].endsWith("raw") ? String.valueOf(value) : formatter.format(value))
                        .orElseGet(MainConfig.NULL_DISPLAY_NAME::getValue);
            default:
                break;
        }

        return null;
    }
}
