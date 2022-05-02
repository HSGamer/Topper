package me.hsgamer.topper.spigot.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import org.bukkit.OfflinePlayer;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderTopHolder extends AutoUpdateTopHolder {
    private static final Pattern PATTERN = Pattern.compile("(\\[.*])?\\s*(.*)\\s*");
    private final String placeholder;
    private final boolean isOnlineOnly;
    private final boolean isAsync;

    public PlaceholderTopHolder(TopperPlugin instance, TopStorage topStorage, String name, String placeholder) {
        super(instance, topStorage, name);
        Matcher matcher = PATTERN.matcher(placeholder);
        this.placeholder = matcher.group(2);
        String prefix = matcher.group(1).toLowerCase(Locale.ROOT);
        isOnlineOnly = prefix.contains("[online]");
        isAsync = prefix.contains("[async]");
    }

    @Override
    public CompletableFuture<Optional<Double>> updateNewValue(UUID uuid) {
        CompletableFuture<Optional<Double>> future = new CompletableFuture<>();
        OfflinePlayer player = instance.getServer().getOfflinePlayer(uuid);
        if (player.isOnline() || !isOnlineOnly) {
            Runnable runnable = () -> {
                try {
                    String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                    future.complete(Optional.of(Double.parseDouble(parsed)));
                } catch (Exception e) {
                    future.complete(Optional.empty());
                }
            };
            if (isAsync) {
                instance.getServer().getScheduler().runTaskAsynchronously(instance, runnable);
            } else {
                instance.getServer().getScheduler().runTask(instance, runnable);
            }
        } else {
            future.complete(Optional.empty());
        }
        return future;
    }

    @Override
    public void onPostRegister() {
        if (Boolean.TRUE.equals(MainConfig.LOAD_ALL_OFFLINE_PLAYERS.getValue())) {
            instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                    getOrCreateEntry(player.getUniqueId());
                }
            });
        }
    }
}
