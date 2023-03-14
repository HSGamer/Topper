package me.hsgamer.topper.placeholderleaderboard.holder;

import me.hsgamer.topper.core.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.core.agent.storage.StorageAgent;
import me.hsgamer.topper.core.agent.update.UpdateAgent;
import me.hsgamer.topper.core.holder.DataWithAgentHolder;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NumberTopHolder extends DataWithAgentHolder<Double> {
    protected final TopperPlaceholderLeaderboard instance;
    private final UpdateAgent<Double, BukkitTask> updateAgent;
    private final StorageAgent<Double, BukkitTask> storageAgent;
    private final SnapshotAgent<Double, BukkitTask> snapshotAgent;

    protected NumberTopHolder(TopperPlaceholderLeaderboard instance, String name) {
        super(name);
        this.instance = instance;

        this.updateAgent = new UpdateAgent<>(this);
        updateAgent.setMaxEntryPerCall(instance.getMainConfig().getTaskSaveEntryPerTick());
        updateAgent.setUpdateFunction(this::updateNewValue);
        updateAgent.setRunTaskFunction(runnable -> {
            int updateDelay = instance.getMainConfig().getTaskUpdateDelay();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, updateDelay, updateDelay);
        });
        updateAgent.setCancelTaskConsumer(BukkitTask::cancel);
        addAgent(updateAgent);

        this.storageAgent = new StorageAgent<>(instance.getTopManager().getStorageSupplier().apply(this));
        storageAgent.setMaxEntryPerCall(instance.getMainConfig().getTaskSaveEntryPerTick());
        storageAgent.setRunTaskFunction(runnable -> {
            int saveDelay = instance.getMainConfig().getTaskSaveDelay();
            return instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, saveDelay, saveDelay);
        });
        storageAgent.setCancelTaskConsumer(BukkitTask::cancel);
        storageAgent.addOnLoadListener(() -> {
            if (instance.getMainConfig().isLoadAllOfflinePlayers()) {
                instance.getServer().getScheduler().scheduleSyncDelayedTask(instance, () -> {
                    for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                        getOrCreateEntry(player.getUniqueId());
                    }
                });
            }
        });
        addAgent(storageAgent);

        this.snapshotAgent = new SnapshotAgent<>(this);
        snapshotAgent.setRunTaskFunction(runnable -> instance.getServer().getScheduler().runTaskTimerAsynchronously(instance, runnable, 20L, 20L));
        snapshotAgent.setCancelTaskConsumer(BukkitTask::cancel);
        snapshotAgent.setComparator(Double::compare);
        addAgent(snapshotAgent);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    protected abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

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
