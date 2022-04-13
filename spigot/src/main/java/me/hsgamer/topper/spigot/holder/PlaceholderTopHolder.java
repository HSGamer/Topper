package me.hsgamer.topper.spigot.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlaceholderTopHolder extends AutoUpdateTopHolder {
    private final String placeholder;

    public PlaceholderTopHolder(TopperPlugin instance, TopStorage topStorage, String name, String placeholder) {
        super(instance, topStorage, name);
        this.placeholder = placeholder;
    }

    @Override
    public CompletableFuture<Optional<Double>> updateNewValue(UUID uuid) {
        CompletableFuture<Optional<Double>> future = new CompletableFuture<>();
        OfflinePlayer player = instance.getServer().getOfflinePlayer(uuid);
        if (player.isOnline() || !MainConfig.ONLINE_PLACEHOLDERS.getValue().contains(getName())) {
            instance.getServer().getScheduler().runTask(instance, () -> {
                try {
                    String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                    future.complete(Optional.of(Double.parseDouble(parsed)));
                } catch (Exception e) {
                    future.complete(Optional.empty());
                }
            });
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
