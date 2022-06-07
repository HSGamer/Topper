package me.hsgamer.topper.placeholderleaderboard.holder;

import me.hsgamer.topper.core.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.core.agent.storage.StorageAgent;
import me.hsgamer.topper.core.agent.update.UpdateAgent;
import me.hsgamer.topper.core.holder.DataHolder;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NumberTopHolder extends DataHolder<Double> {
    protected final TopperPlaceholderLeaderboard instance;
    private final UpdateAgent<Double, BukkitTask> updateAgent;
    private final StorageAgent<Double, BukkitTask> storageAgent;
    private final SnapshotAgent<Double, BukkitTask> snapshotAgent;

    protected NumberTopHolder(TopperPlaceholderLeaderboard instance, String name) {
        super(name);
        this.instance = instance;
        this.updateAgent = new UpdateAgent<>(this);
        this.storageAgent = new StorageAgent<>(instance.getTopManager().getStorageSupplier().apply(this));
        this.snapshotAgent = new SnapshotAgent<>(this);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    protected abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

    @Override
    public void onRegister() {
        updateAgent.setMaxEntryPerCall(MainConfig.TASK_UPDATE_ENTRY_PER_TICK.getValue());
        updateAgent.setUpdateFunction(this::updateNewValue);
        updateAgent.setRunTaskFunction(runnable -> {
            int updateDelay = MainConfig.TASK_UPDATE_DELAY.getValue();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, updateDelay, updateDelay);
        });
        updateAgent.setCancelTaskConsumer(BukkitTask::cancel);
        updateAgent.start();

        storageAgent.setMaxEntryPerCall(MainConfig.TASK_SAVE_ENTRY_PER_TICK.getValue());
        storageAgent.setRunTaskFunction(runnable -> {
            int saveDelay = MainConfig.TASK_SAVE_DELAY.getValue();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, saveDelay, saveDelay);
        });
        storageAgent.setCancelTaskConsumer(BukkitTask::cancel);
        storageAgent.addOnLoadListener(() -> {
            if (Boolean.TRUE.equals(MainConfig.LOAD_ALL_OFFLINE_PLAYERS.getValue())) {
                instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                        getOrCreateEntry(player.getUniqueId());
                    }
                });
            }
        });
        storageAgent.start();

        snapshotAgent.setRunTaskFunction(runnable -> instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, 20L, 20L));
        snapshotAgent.setCancelTaskConsumer(BukkitTask::cancel);
        snapshotAgent.start();
    }

    @Override
    public void onUnregister() {
        updateAgent.stop();
        snapshotAgent.stop();
        storageAgent.stop();
        super.onUnregister();
    }

    public UpdateAgent<Double, BukkitTask> getUpdateAgent() {
        return updateAgent;
    }

    public StorageAgent<Double, BukkitTask> getStorageAgent() {
        return storageAgent;
    }

    public SnapshotAgent<Double, BukkitTask> getSnapshotAgent() {
        return snapshotAgent;
    }
}
