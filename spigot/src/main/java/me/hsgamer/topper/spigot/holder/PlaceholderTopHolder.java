package me.hsgamer.topper.spigot.holder;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hsgamer.topper.core.TopStorage;
import me.hsgamer.topper.spigot.TopperPlugin;
import me.hsgamer.topper.spigot.config.MainConfig;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
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
    public CompletableFuture<Optional<BigDecimal>> updateNewValue(UUID uuid) {
        CompletableFuture<Optional<BigDecimal>> future = new CompletableFuture<>();
        instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
            OfflinePlayer player = instance.getServer().getOfflinePlayer(uuid);
            try {
                String parsed = PlaceholderAPI.setPlaceholders(player, placeholder);
                future.complete(Optional.of(new BigDecimal(parsed)));
            } catch (Exception e) {
                future.complete(Optional.empty());
            }
        });
        return future;
    }

    @Override
    public void onPostRegister() {
        if (Boolean.TRUE.equals(MainConfig.LOAD_ALL_OFFLINE_PLAYERS.getValue())) {
            for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                getOrCreateEntry(player.getUniqueId());
            }
        }
    }
}
