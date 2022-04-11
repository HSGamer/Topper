package me.hsgamer.topper.spigot.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.topper.core.TopEntry;
import me.hsgamer.topper.core.TopHolder;
import me.hsgamer.topper.spigot.TopperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
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
        Optional<TopHolder> optionalHolder = instance.getTopManager().getTopHolder(args[0]);
        if (!optionalHolder.isPresent()) return null;
        TopHolder holder = optionalHolder.get();

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
                        .map(TopEntry::getUuid)
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .orElse("");
            }
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
                        .map(TopEntry::getValue)
                        .map(BigDecimal::toPlainString)
                        .orElse("");
            }
            case "top_rank":
                return Integer.toString(holder.getTopIndex(player.getUniqueId()) + 1);
            case "value":
                return holder.getEntry(player.getUniqueId())
                        .map(TopEntry::getValue)
                        .map(BigDecimal::toPlainString)
                        .orElse("");
            default:
                break;
        }

        return null;
    }
}
