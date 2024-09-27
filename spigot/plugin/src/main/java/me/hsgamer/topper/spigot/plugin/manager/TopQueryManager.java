package me.hsgamer.topper.spigot.plugin.manager;

import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.spigot.plugin.TopperPlugin;
import me.hsgamer.topper.spigot.plugin.config.MessageConfig;
import me.hsgamer.topper.spigot.plugin.holder.NumberTopHolder;
import me.hsgamer.topper.spigot.query.number.NumberQueryManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class TopQueryManager extends NumberQueryManager<UUID, Double, NumberTopHolder, OfflinePlayer> {
    private final TopperPlugin instance;

    public TopQueryManager(TopperPlugin instance) {
        this.instance = instance;
    }

    @Override
    protected Optional<NumberTopHolder> getHolder(String name) {
        return instance.get(TopManager.class).getTopHolder(name);
    }

    @Override
    protected @NotNull SnapshotAgent<UUID, Double> getSnapshotAgent(@NotNull NumberTopHolder holder) {
        return holder.getSnapshotAgent();
    }

    @Override
    protected @NotNull String getDisplayName(@Nullable UUID key) {
        return instance.get(MessageConfig.class).getDisplayName(key);
    }

    @Override
    protected @NotNull String getDisplayValue(@Nullable Double value) {
        return instance.get(MessageConfig.class).getDisplayValue(value);
    }

    @Override
    protected @NotNull String getDisplayKey(@Nullable UUID key) {
        return instance.get(MessageConfig.class).getDisplayUuid(key);
    }

    @Override
    protected @NotNull String getDisplayRawValue(@Nullable Double value) {
        return instance.get(MessageConfig.class).getDisplayRawValue(value);
    }

    @Override
    protected @NotNull UUID getKeyFromActor(@NotNull OfflinePlayer actor) {
        return actor.getUniqueId();
    }
}
