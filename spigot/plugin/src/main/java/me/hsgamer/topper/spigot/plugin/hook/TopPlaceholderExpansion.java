package me.hsgamer.topper.spigot.plugin.hook;

import io.github.projectunified.minelib.plugin.base.Loadable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hsgamer.topper.core.DataEntry;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.plugin.manager.TopManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class TopPlaceholderExpansion extends PlaceholderExpansion implements Loadable {
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
    public void enable() {
        this.register();
    }

    @Override
    public void disable() {
        this.unregister();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] args = params.split(";");
        if (args.length < 2) return null;
        Optional<NumberTopHolder> optionalHolder = instance.get(TopManager.class).getTopHolder(args[0]);
        if (!optionalHolder.isPresent()) return null;
        NumberTopHolder holder = optionalHolder.get();

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
                        .map(DataEntry::getKey)
                        .map(Bukkit::getOfflinePlayer)
                        .map(OfflinePlayer::getName)
                        .orElseGet(() -> "---"); // TODO: Get the default value from the config
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
                        .map(Objects::toString)
                        .orElseGet(() -> "---"); // TODO: Get the default value from the config
            }
            case "top_rank":
                return Integer.toString(holder.getSnapshotAgent().getSnapshotIndex(player.getUniqueId()) + 1);
            case "value":
            case "value_raw":
                return holder.getEntry(player.getUniqueId())
                        .map(DataEntry::getValue)
                        .map(Objects::toString)
                        .orElseGet(() -> "---"); // TODO: Get the default value from the config
            default:
                break;
        }

        return null;
    }
}
