package me.hsgamer.topper.spigot.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.topper.core.common.DataEntry;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import me.hsgamer.topper.spigot.formatter.TopFormatter;
import me.hsgamer.topper.spigot.holder.PlaceholderTopHolder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TopPlaceholderExpansion extends PlaceholderExpansion {
    private final TopperPlugin instance;

    public TopPlaceholderExpansion(TopperPlugin instance) {
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
        Optional<PlaceholderTopHolder> optionalHolder = instance.getTopManager().getTopHolder(args[0]);
        if (!optionalHolder.isPresent()) return null;
        PlaceholderTopHolder holder = optionalHolder.get();
        TopFormatter formatter = instance.getTopManager().getTopFormatter(args[0]);

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
                return holder.getEntryByIndex(i - 1)
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
                return holder.getEntryByIndex(i - 1)
                        .map(DataEntry::getValue)
                        .map(value -> args[1].endsWith("raw") ? String.valueOf(value) : formatter.format(value))
                        .orElseGet(MainConfig.NULL_DISPLAY_VALUE::getValue);
            }
            case "top_rank":
                return Integer.toString(holder.getTopIndex(player.getUniqueId()) + 1);
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
