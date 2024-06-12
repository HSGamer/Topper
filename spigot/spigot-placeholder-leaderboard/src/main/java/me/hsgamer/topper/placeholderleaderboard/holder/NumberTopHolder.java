package me.hsgamer.topper.placeholderleaderboard.holder;

import io.github.projectunified.minelib.scheduler.async.AsyncScheduler;
import io.github.projectunified.minelib.scheduler.global.GlobalScheduler;
import me.hsgamer.topper.agent.agent.update.UpdateAgent;
import me.hsgamer.topper.agent.snapshot.SnapshotAgent;
import me.hsgamer.topper.agent.storage.StorageAgent;
import me.hsgamer.topper.core.holder.DataWithAgentHolder;
import me.hsgamer.topper.placeholderleaderboard.TopperPlaceholderLeaderboard;
import me.hsgamer.topper.placeholderleaderboard.config.MainConfig;
import me.hsgamer.topper.placeholderleaderboard.manager.TopManager;
import org.bukkit.OfflinePlayer;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class NumberTopHolder extends DataWithAgentHolder<UUID, Double> {
    protected final TopperPlaceholderLeaderboard instance;
    private final UpdateAgent<UUID, Double> updateAgent;
    private final StorageAgent<UUID, Double> storageAgent;
    private final SnapshotAgent<UUID, Double> snapshotAgent;

    protected NumberTopHolder(TopperPlaceholderLeaderboard instance, String name) {
        super(name);
        this.instance = instance;

        this.updateAgent = new UpdateAgent<>(this);
        updateAgent.setMaxEntryPerCall(instance.get(MainConfig.class).getTaskUpdateEntryPerTick());
        updateAgent.setUpdateFunction(this::updateNewValue);
        updateAgent.setRunTaskFunction(runnable -> {
            int updateDelay = instance.get(MainConfig.class).getTaskUpdateDelay();
            return AsyncScheduler.get(instance).runTimer(runnable, updateDelay, updateDelay)::cancel;
        });
        addAgent(updateAgent);

        this.storageAgent = new StorageAgent<>(instance.getLogger(), instance.get(TopManager.class).getStorageSupplier().getStorage(this));
        storageAgent.setMaxEntryPerCall(instance.get(MainConfig.class).getTaskSaveEntryPerTick());
        storageAgent.setRunTaskFunction(runnable -> {
            int saveDelay = instance.get(MainConfig.class).getTaskSaveDelay();
            return AsyncScheduler.get(instance).runTimer(runnable, saveDelay, saveDelay)::cancel;
        });
        getListenerManager().add(StorageAgent.LOAD_EVENT, () -> {
            if (instance.get(MainConfig.class).isLoadAllOfflinePlayers()) {
                GlobalScheduler.get(instance).run(() -> {
                    for (OfflinePlayer player : instance.getServer().getOfflinePlayers()) {
                        getOrCreateEntry(player.getUniqueId());
                    }
                });
            }
        });
        addAgent(storageAgent);

        this.snapshotAgent = new SnapshotAgent<>(this);
        snapshotAgent.setRunTaskFunction(runnable -> AsyncScheduler.get(instance).runTimer(runnable, 20L, 20L)::cancel);
        snapshotAgent.setComparator(Comparator.reverseOrder());
        addAgent(snapshotAgent);
    }

    @Override
    public Double getDefaultValue() {
        return 0D;
    }

    protected abstract CompletableFuture<Optional<Double>> updateNewValue(UUID uuid);

    public UpdateAgent<UUID, Double> getUpdateAgent() {
        return updateAgent;
    }

    public StorageAgent<UUID, Double> getStorageAgent() {
        return storageAgent;
    }

    public SnapshotAgent<UUID, Double> getSnapshotAgent() {
        return snapshotAgent;
    }
}
