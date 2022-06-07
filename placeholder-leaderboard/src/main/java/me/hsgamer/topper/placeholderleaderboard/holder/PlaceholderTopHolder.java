package me.hsgamer.topper.placeholderleaderboard.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.core.storage.DataStorage;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderTopHolder extends NumberTopHolder {
    private static final Pattern PATTERN = Pattern.compile("(\\[.*])?\\s*(.*)\\s*");
    private final String placeholder;
    private final boolean isOnlineOnly;
    private final boolean isAsync;

    public PlaceholderTopHolder(TopperPlaceholderLeaderboard instance, Function<DataHolder<Double>, DataStorage<Double>> storageSupplier, String name, String placeholder) {
        super(instance, storageSupplier, name);
        Matcher matcher = PATTERN.matcher(placeholder);
        if (matcher.matches()) {
            this.placeholder = Optional.ofNullable(matcher.group(2)).orElse("");
            String prefix = Optional.ofNullable(matcher.group(1)).map(String::toLowerCase).orElse("");
            isOnlineOnly = prefix.contains("[online]");
            isAsync = prefix.contains("[async]");
        } else {
            this.placeholder = placeholder;
            isOnlineOnly = false;
            isAsync = false;
        }
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
