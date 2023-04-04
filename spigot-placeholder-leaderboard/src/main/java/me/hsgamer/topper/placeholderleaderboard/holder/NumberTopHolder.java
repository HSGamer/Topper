package me.hsgamer.topper.placeholderleaderboard.holder;

import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
import me.hsgamer.topper.core.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.core.agent.storage.StorageAgent;
import me.hsgamer.topper.core.agent.update.UpdateAgent;
import me.hsgamer.topper.core.holder.DataWithAgentHolder;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NumberTopHolder extends DataWithAgentHolder<Double> {
    protected final TopperPlaceholderLeaderboard instance;
    private final UpdateAgent<Double> updateAgent;
    private final StorageAgent<Double> storageAgent;
    private final SnapshotAgent<Double> snapshotAgent;

    protected NumberTopHolder(TopperPlaceholderLeaderboard instance, String name) {
        super(name);
        this.instance = instance;

        this.updateAgent = new UpdateAgent<>(this);
        updateAgent.setMaxEntryPerCall(instance.getMainConfig().getTaskSaveEntryPerTick());
        updateAgent.setUpdateFunction(this::updateNewValue);
        updateAgent.setRunTaskFunction(runnable -> {
            int updateDelay = instance.getMainConfig().getTaskUpdateDelay();
            return Scheduler.CURRENT.runTaskTimer(instance, runnable, updateDelay, updateDelay, true)::cancel;
        });
        addAgent(updateAgent);

        this.storageAgent = new StorageAgent<>(instance.getLogger(), instance.getTopManager().getStorageSupplier().apply(this));
        storageAgent.setMaxEntryPerCall(instance.getMainConfig().getTaskSaveEntryPerTick());
        storageAgent.setRunTaskFunction(runnable -> {
            int saveDelay = instance.getMainConfig().getTaskSaveDelay();
            return Scheduler.CURRENT.runTaskTimer(instance, runnable, saveDelay, saveDelay, true)::cancel;
        });
        storageAgent.addOnLoadListener(() -> {
            if (instance.getMainConfig().isLoadAllOfflinePlayers()) {
                Scheduler.CURRENT.runTask(instance, () -> {
                    for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                        getOrCreateEntry(player.getUniqueId());
                    }
                }, false);
            }
        });
        addAgent(storageAgent);

        this.snapshotAgent = new SnapshotAgent<>(this);
        snapshotAgent.setRunTaskFunction(runnable -> Scheduler.CURRENT.runTaskTimer(instance, runnable, 20L, 20L, true)::cancel);
        snapshotAgent.setComparator(Double::compare);
        addAgent(snapshotAgent);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    protected abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

    public UpdateAgent<Double> getUpdateAgent() {
        return updateAgent;
    }

    public StorageAgent<Double> getStorageAgent() {
        return storageAgent;
    }

    public SnapshotAgent<Double> getSnapshotAgent() {
        return snapshotAgent;
    }
}
